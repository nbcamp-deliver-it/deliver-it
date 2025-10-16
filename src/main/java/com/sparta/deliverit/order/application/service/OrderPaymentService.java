package com.sparta.deliverit.order.application.service;

import com.sparta.deliverit.order.application.dto.CreateOrderCommand;
import com.sparta.deliverit.order.domain.entity.Order;
import com.sparta.deliverit.order.presentation.dto.response.OrderPaymentResponse;
import com.sparta.deliverit.order.presentation.dto.response.OrderResponse;
import com.sparta.deliverit.payment.application.service.PaymentService;
import com.sparta.deliverit.payment.domain.entity.Payment;
import com.sparta.deliverit.payment.presentation.dto.PaymentRequestDto;
import com.sparta.deliverit.payment.presentation.dto.PaymentResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;

@Slf4j(topic = "orderPaymentService")
@Service
public class OrderPaymentService {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final Clock clock;

    @Autowired
    public OrderPaymentService(OrderService orderService, PaymentService paymentService, Clock clock) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.clock = clock;
    }

    public OrderPaymentResponse checkout(CreateOrderCommand createOrderCommand, PaymentRequestDto paymentRequest, Long userId) {

        // 주문 생성
        Order order = orderService.createOrderForPayment(createOrderCommand, userId);

        String restaurantId = createOrderCommand.getRestaurantId();
        String orderId = order.getOrderId();
        Long orderVersion = order.getVersion();

        Payment payment = null;
        try {
            // 결제 생성, 결제 실행, 결제 결과 저장
            payment = paymentService.delegateRequest(paymentRequest);
        } catch (Exception e) {
            orderService.failIfValid(orderId, orderVersion);

            Order freshOrder = orderService.loadFresh(orderId);
            return OrderPaymentResponse.create(
                    orderId,
                    "",
                    "결제에 실패했습니다.",
                    OrderResponse.of(freshOrder),
                    PaymentResponseDto.fail(
                            "NO_DATA",
                            paymentRequest.getCardNum(),
                            paymentRequest.getCompany(),
                            ZonedDateTime.now(clock)
                    )
            );
        }

        String paymentId = payment.getPaymentId();
        // DB에서 레스토랑, 메뉴 상태까지 함께 검사 진행
        int updated = orderService.completeIfValid(orderId, paymentId, orderVersion);
        if (updated == 0) {
            try {
                // 결제 취소
                paymentService.paymentCancel(order);

            } catch (Exception pe) {
                log.error("paymentCancel failed paymentId={}, orderId={}, msg={}", paymentId, orderId, pe.getMessage(), pe);
            }

            // 주문 상태 ORDER_FAIL로 변경
            orderService.failIfValid(orderId, orderVersion);

            Order freshOrder = orderService.loadFresh(orderId);
            return OrderPaymentResponse.create(
                    orderId,
                    "",
                    "결제에 실패했습니다.",
                    OrderResponse.of(freshOrder),
                    PaymentResponseDto.fail(
                            payment.getPaymentId(),
                            payment.getCardNum(),
                            payment.getCardCompany(),
                            payment.getPaidAt()
                    )
            );
        }

        Order freshOrder = orderService.loadFresh(orderId);
        return OrderPaymentResponse.create(
                orderId,
                "",
                "결제가 완료되었습니다.",
                OrderResponse.of(freshOrder),
                PaymentResponseDto.of(payment));
    }

}