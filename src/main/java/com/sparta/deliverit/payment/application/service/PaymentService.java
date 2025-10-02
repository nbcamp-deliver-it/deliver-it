package com.sparta.deliverit.payment.application.service;

import com.sparta.deliverit.payment.domain.entity.Payment;
import com.sparta.deliverit.payment.domain.repository.PaymentRepository;
import com.sparta.deliverit.payment.enums.Company;
import com.sparta.deliverit.payment.presentation.dto.PaymentRequestDto;
import com.sparta.deliverit.payment.presentation.dto.PaymentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
//    private final OrderRepository orderRepository;
    private final List<PaymentProcessor> processorList;

    @Transactional
    public PaymentResponseDto delegateRequest(String orderId, PaymentRequestDto requestDto) {
//        verifyPaymentRequest(orderId, requestDto);

        PaymentProcessor processor = getProcessor(Company.valueOf(requestDto.getCompany()));
        Payment entity = processor.paymentRequest(requestDto);
        Payment payment = paymentRepository.save(entity);

        return PaymentResponseDto.of(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(String orderId, String paymentId) {
//        verifyOrder(orderId, paymentId);
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(IllegalArgumentException::new);
        return PaymentResponseDto.of(payment);
    }

    @Transactional
    public PaymentResponseDto deletePayment(String orderId, String paymentId) {
        return null;
    }

//    private boolean verifyPaymentRequest(String orderId, PaymentRequestDto requestDto) {
//        Order order = orderRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);
//        if(order.getOrderStatus() != OrderStatus.CREATED ||
//                !order.getTotalPrice().equals(requestDto.getTotalPrice()))
//            throw new IllegalArgumentException();
//
//        return true;
//    }
//
//    private boolean verifyOrder(String orderId, String paymentId) {
//        Order order = orderRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);
//        if(!order.getPayment().getPaymentId().equals(paymentId))
//            throw new IllegalArgumentException();
//
//        return true;
//    }

    private PaymentProcessor getProcessor(Company company) {
        return processorList.stream()
                .filter(processor -> processor.findByCompany(company))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
