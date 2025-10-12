package com.sparta.deliverit.order.application;

import com.sparta.deliverit.order.presentation.dto.response.OrderInfo;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface OrderService {

    OrderInfo getOrderDetailForUser(String orderId, String userId);

    OrderInfo getOrderDetailForOwner(String orderId, String userId);

    Page<OrderInfo> getOrderListForUser(String userId, LocalDateTime from, LocalDateTime to, int pageNumber, int pageSize);

    Page<OrderInfo> getOrderListForOwner(String userId, String restaurantId ,LocalDateTime from, LocalDateTime to, int pageNumber, int pageSize);
}