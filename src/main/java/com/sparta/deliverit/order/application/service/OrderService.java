package com.sparta.deliverit.order.application.service;

import com.sparta.deliverit.order.application.dto.CreateOrderCommand;
import com.sparta.deliverit.order.presentation.dto.response.CancelOrderInfo;
import com.sparta.deliverit.order.presentation.dto.response.ConfirmOrderInfo;
import com.sparta.deliverit.order.presentation.dto.response.CreateOrderInfo;
import com.sparta.deliverit.order.presentation.dto.response.OrderInfo;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface OrderService {

    OrderInfo getOrderDetailForUser(String orderId, String userId);

    OrderInfo getOrderDetailForOwner(String orderId, String userId);

    Page<OrderInfo> getOrderListForUser(String userId, LocalDateTime from, LocalDateTime to, int pageNumber, int pageSize);

    Page<OrderInfo> getOrderListForOwner(String userId, String restaurantId ,LocalDateTime from, LocalDateTime to, int pageNumber, int pageSize);

    ConfirmOrderInfo confirmOrder(String restaurantId, String orderId, String accessUserId);

    CancelOrderInfo cancelOrderForUser(String orderId, String userId);

    CancelOrderInfo cancelOrderForOwner(String restaurantId, String orderId, String accessUserId);

    CreateOrderInfo createOrder(CreateOrderCommand orderCommand, Long userId);
}