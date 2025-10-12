package com.sparta.deliverit.order.application;

import com.sparta.deliverit.global.response.code.OrderResponseCode;
import com.sparta.deliverit.order.domain.entity.OrderItem;
import com.sparta.deliverit.order.exception.NotFoundOrderException;
import com.sparta.deliverit.order.exception.NotFoundOrderItemException;
import com.sparta.deliverit.order.infrastructure.OrderItemRepository;
import com.sparta.deliverit.order.infrastructure.OrderRepository;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForOwner;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForUser;
import com.sparta.deliverit.order.presentation.dto.response.MenuInfo;
import com.sparta.deliverit.order.presentation.dto.response.OrderInfo;
import com.sparta.deliverit.payment.application.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentService paymentService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentService = paymentService;
    }

    @Override
    public OrderInfo getOrderDetailForUser(String orderId, String userId) {

        OrderDetailForUser orderDetail = orderRepository.getByOrderIdForUser(orderId)
                .orElseThrow(
                        () -> new NotFoundOrderException(OrderResponseCode.NOT_FOUND_ORDER)
                );

        if (!userId.equals(orderDetail.getUserId())) {
            throw new AccessDeniedException("해당 주문에 접근할 권한이 없습니다.");
        }

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrder(orderId);
        if (orderItemList.isEmpty()) {
            throw new NotFoundOrderItemException(OrderResponseCode.NOT_FOUND_ORDER_ITEM);
        }

        List<MenuInfo> menuInfoList = orderItemList.stream()
                .map(MenuInfo::of)
                .toList();

        return OrderInfo.of(orderDetail, menuInfoList);
    }

    @Override
    public OrderInfo getOrderDetailForOwner(String orderId, String userId) {
        OrderDetailForOwner orderDetail = orderRepository.getByOrderIdForOwner(orderId)
                .orElseThrow(
                        () -> new NotFoundOrderException(OrderResponseCode.NOT_FOUND_ORDER)
                );

        if (!userId.equals(orderDetail.getRestaurantUserId())) {
            throw new AccessDeniedException("현재 사장님이 접근할 수 없는 주문입니다.");
        }

        List<OrderItem> orderItemList = orderItemRepository.findAllByOrder(orderDetail.getOrderId());

        if (orderItemList.isEmpty()) {
            throw new NotFoundOrderItemException(OrderResponseCode.NOT_FOUND_ORDER_ITEM);
        }

        List<MenuInfo> menuInfoList = orderItemList.stream()
                .map(MenuInfo::of)
                .toList();

        return OrderInfo.of(orderDetail, menuInfoList);
    }
}