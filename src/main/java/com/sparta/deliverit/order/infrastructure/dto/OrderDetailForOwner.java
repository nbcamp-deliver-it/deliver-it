package com.sparta.deliverit.order.infrastructure.dto;

import com.sparta.deliverit.order.domain.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderDetailForOwner {
    String getUserId();
    String getUserName();
    String getRestaurantId();
    String getRestaurantName();
    String getRestaurantUserId();
    String getOrderId();
    LocalDateTime getOrderedAt();
    OrderStatus getOrderStatus();
    String getAddress();
    BigDecimal getTotalPrice();
    Long getVersion();
    String getPaymentId();
}
