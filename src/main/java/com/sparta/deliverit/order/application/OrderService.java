package com.sparta.deliverit.order.application;

import com.sparta.deliverit.order.presentation.dto.response.OrderInfo;

public interface OrderService {

    OrderInfo getOrderDetailForUser(String orderId, String userId);

    OrderInfo getOrderDetailForOwner(String orderId, String userId);
}