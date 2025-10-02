package com.sparta.deliverit.order.presentation.controller;

import com.sparta.deliverit.global.presentation.dto.Result;
import com.sparta.deliverit.order.presentation.dto.response.CancelOrderInfo;
import com.sparta.deliverit.order.presentation.dto.request.CreateOrderRequest;
import com.sparta.deliverit.order.presentation.dto.response.ConfirmOrderInfo;
import com.sparta.deliverit.order.presentation.dto.response.CreateOrderInfo;
import com.sparta.deliverit.order.presentation.dto.response.OrderInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface OrderController {

    Result<List<OrderInfo>> getOrderList(Authentication userAuthInfo);

    Result<OrderInfo> getOrder(
            @NotBlank
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "UUID 형식이 올바르지 않습니다.")
            @PathVariable String orderId,
            Authentication userAuthInfo);

    Result<CreateOrderInfo> createOrder(@RequestBody @Valid CreateOrderRequest request);

    Result<ConfirmOrderInfo> confirmOrder(
            @NotBlank
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "UUID 형식이 올바르지 않습니다.")
            @PathVariable
            String orderId);

    Result<CancelOrderInfo> cancelOrder(
            @NotBlank
            @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
                    message = "UUID 형식이 올바르지 않습니다.")
            @PathVariable
            String orderId);
}
