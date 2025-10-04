package com.sparta.deliverit.order.presentation.controller;

import com.sparta.deliverit.global.response.ApiResponse;
import com.sparta.deliverit.order.presentation.dto.response.CancelOrderInfo;
import com.sparta.deliverit.order.presentation.dto.request.CreateOrderRequest;
import com.sparta.deliverit.order.presentation.dto.response.ConfirmOrderInfo;
import com.sparta.deliverit.order.presentation.dto.response.CreateOrderInfo;
import com.sparta.deliverit.order.presentation.dto.response.OrderInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface OrderController {

    ApiResponse<List<OrderInfo>> getOrderList(Authentication userAuthInfo);

    ApiResponse<OrderInfo> getOrder(
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "주문의 UUID 형식이 올바르지 않습니다.")
            @PathVariable String orderId,
            Authentication userAuthInfo);

    ApiResponse<CreateOrderInfo> createOrder(@RequestBody @Valid CreateOrderRequest request);

    ApiResponse<ConfirmOrderInfo> confirmOrder(
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "주문의 UUID 형식이 올바르지 않습니다.")
            @PathVariable
            String orderId);

    ApiResponse<CancelOrderInfo> cancelOrder(
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "주문의 UUID 형식이 올바르지 않습니다.")
            @PathVariable
            String orderId);
}
