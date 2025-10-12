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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public Page<OrderInfo> getOrderListForUser(String userId, LocalDateTime from, LocalDateTime to, int pageNumber, int pageSize) {
        Page<OrderDetailForUser> orderdetailPage = orderRepository.findOrdersByUserIdForUser(userId, from, to, PageRequest.of(pageNumber, pageSize));
        List<OrderDetailForUser> orderDetailList = orderdetailPage.getContent();

        boolean checkAccess = orderDetailList.stream()
                .map(OrderDetailForUser::getUserId)
                .allMatch(userId::equals);

        if (!checkAccess) {
            throw new AccessDeniedException("주문 목록에 접근할 권한이 없습니다.");
        }

        Set<String> orderIds = orderDetailList.stream()
                .map(OrderDetailForUser::getOrderId)
                .collect(Collectors.toSet());

        Map<String, List<OrderItem>> itemsByOrderId = orderItemRepository.findAllByOrderIn(orderIds).stream()
                .collect(Collectors.groupingBy(orderItem -> orderItem.getOrder().getOrderId()));


        return orderdetailPage.map(o -> {
            List<OrderItem> orderItemList = itemsByOrderId.getOrDefault(o.getOrderId(), List.of());
            if (orderItemList.isEmpty()) {
                throw new NotFoundOrderItemException(OrderResponseCode.NOT_FOUND_ORDER_ITEM);
            }

            List<MenuInfo> menuInfoList = orderItemList.stream()
                    .map(MenuInfo::of)
                    .toList();

            return OrderInfo.of(o, menuInfoList);
        });
    }

    @Override
    public Page<OrderInfo> getOrderListForOwner(String userId, String restaurantId, LocalDateTime from, LocalDateTime to, int pageNumber, int pageSize) {
        Page<OrderDetailForOwner> orderDetailPage = orderRepository.findOrdersByRestaurantIdForOwner(restaurantId, from, to, PageRequest.of(pageNumber, pageSize));
        List<OrderDetailForOwner> orderDetailList = orderDetailPage.getContent();

        boolean checkAccess = orderDetailList.stream()
                .map(OrderDetailForOwner::getRestaurantUserId)
                .allMatch(userId::equals);

        if (!checkAccess) {
            throw new AccessDeniedException("해당 음식점은 접근할 수 없는 주문 목록입니다.");
        }

        Set<String> orderIds = orderDetailList.stream()
                .map(OrderDetailForOwner::getOrderId)
                .collect(Collectors.toSet());

        Map<String, List<OrderItem>> itemsByOrderId = orderItemRepository.findAllByOrderIn(orderIds).stream()
                .collect(Collectors.groupingBy(orderItem -> orderItem.getOrder().getOrderId()));

        return orderDetailPage.map(o -> {
            List<OrderItem> orderItemList = itemsByOrderId.getOrDefault(o.getOrderId(), List.of());
            if (orderItemList.isEmpty()) {
                throw new NotFoundOrderItemException(OrderResponseCode.NOT_FOUND_ORDER_ITEM);
            }

            List<MenuInfo> menuInfoList = orderItemList.stream()
                    .map(MenuInfo::of)
                    .toList();

            return OrderInfo.of(o, menuInfoList);
        });
    }
}