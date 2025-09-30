package com.sparta.deliverit.order.controller;

import com.sparta.deliverit.common.dto.Result;
import com.sparta.deliverit.order.dto.request.CreateOrderRequest;
import com.sparta.deliverit.order.dto.response.CreateOrderResponse;
import com.sparta.deliverit.order.dto.response.OrderListResponse;
import com.sparta.deliverit.order.dto.response.OrderResponse;

public interface OrderController {

    Result<OrderListResponse> getOrderList();

    Result<OrderResponse> getOrder(String orderId);

    Result<CreateOrderResponse> createOrder(CreateOrderRequest request);

}
