package com.sparta.deliverit.order.controller;

import com.sparta.deliverit.common.dto.Result;
import com.sparta.deliverit.order.dto.request.CreateOrderRequest;
import com.sparta.deliverit.order.dto.response.*;

import java.util.List;

public interface OrderController {

    Result<List<OrderInfo>> getOrderList();

    Result<OrderInfo> getOrder(String orderId);

    Result<CreateOrderInfo> createOrder(CreateOrderRequest request);

}
