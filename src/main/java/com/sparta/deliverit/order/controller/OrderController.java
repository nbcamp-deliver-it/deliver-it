package com.sparta.deliverit.order.controller;

import com.sparta.deliverit.common.dto.Result;
import com.sparta.deliverit.order.dto.CancelOrderInfo;
import com.sparta.deliverit.order.dto.request.CreateOrderRequest;
import com.sparta.deliverit.order.dto.response.*;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface OrderController {

    Result<List<OrderInfo>> getOrderList(Authentication userAuthInfo);

    Result<OrderInfo> getOrder(String orderId);

    Result<CreateOrderInfo> createOrder(CreateOrderRequest request);

    Result<ConfirmOrderInfo> confirmOrder(String orderId);

    Result<CancelOrderInfo> cancelOrder(String orderId);
}
