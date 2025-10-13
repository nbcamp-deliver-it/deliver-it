package com.sparta.deliverit.order.presentation.controller;

import com.sparta.deliverit.global.response.ApiResponse;
import com.sparta.deliverit.global.response.code.OrderResponseCode;
import com.sparta.deliverit.order.application.OrderService;
import com.sparta.deliverit.order.application.dto.CreateOrderCommand;
import com.sparta.deliverit.order.presentation.dto.response.CancelOrderInfo;
import com.sparta.deliverit.order.presentation.dto.request.CreateOrderRequest;
import com.sparta.deliverit.order.presentation.dto.response.ConfirmOrderInfo;
import com.sparta.deliverit.order.presentation.dto.response.CreateOrderInfo;
import com.sparta.deliverit.order.presentation.dto.response.OrderInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Validated
@RestController
public class OrderControllerV1 implements OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderControllerV1(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/v1/orders")
    public ApiResponse<Page<OrderInfo>> getOrderListForUser(LocalDateTime from, LocalDateTime to, Integer pageNumber, Integer pageSize) {
        // 임시 로그인
        String userId = "1";

        Page<OrderInfo> orderInfoList = orderService.getOrderListForUser(
                userId,
                from,
                to,
                pageNumber,
                pageSize
        );
        return ApiResponse.create(OrderResponseCode.ORDER_LIST_SUCCESS,"고객 본인의 주문 목록을 조회했습니다.", orderInfoList);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/v1/orders/{orderId}")
    public ApiResponse<OrderInfo> getOrderForUser(String orderId) {
        // 임시 로그인
        String userId = "1";

        OrderInfo orderInfo = orderService.getOrderDetailForUser(orderId, userId);

        return ApiResponse.create(OrderResponseCode.ORDER_DETAIL_SUCCESS,"고객의 주문을 조회했습니다.", orderInfo);
    }


    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/v1/restaurants/{restaurantId}/orders")
    public ApiResponse<Page<OrderInfo>> getOrderListForOwner(String restaurantId, LocalDateTime from, LocalDateTime to, Integer pageNumber, Integer pageSize) {
        // 임시 로그인
        String userId = "1";

        Page<OrderInfo> orderInfoList = orderService.getOrderListForOwner(
                userId,
                restaurantId,
                from,
                to,
                pageNumber,
                pageSize
        );

        return ApiResponse.create(OrderResponseCode.ORDER_LIST_SUCCESS,"음식점에서 주문 목록을 조회했습니다.", orderInfoList);
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/v1/restaurants/{restaurantId}/orders/{orderId}")
    public ApiResponse<OrderInfo> getOrderForOwner(String restaurantId, String orderId) {
        // 임시 로그인
        String userId = "1";

        OrderInfo orderInfo = orderService.getOrderDetailForOwner(orderId, userId);

        return ApiResponse.create(OrderResponseCode.ORDER_DETAIL_SUCCESS,"음식점이 주문을 조회했습니다.", orderInfo);
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/v1/orders")
    public ApiResponse<CreateOrderInfo> createOrder(CreateOrderRequest request) {

        // 임시 로그인
        String userId = "1";

        CreateOrderInfo orderInfo = orderService.createOrder(CreateOrderCommand.of(request), Long.valueOf(userId));
        return ApiResponse.create(OrderResponseCode.ORDER_CREATE_SUCCESS, "주문을 정상적으로 생성했습니다.", orderInfo);
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/v1/restaurants/{restaurantId}/orders/{orderId}/confirm")
    public ApiResponse<ConfirmOrderInfo> confirmOrder(String restaurantId, String orderId) {
        // 임시 로그인
        String userId = "2";

        ConfirmOrderInfo orderInfo = orderService.confirmOrder(restaurantId, orderId, userId);

        return ApiResponse.create(OrderResponseCode.ORDER_CONFIRM_SUCCESS,"주문 확인이 완료되었습니다.", orderInfo);
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @PatchMapping("/v1/orders/{orderId}")
    public ApiResponse<CancelOrderInfo> cancelOrderForUser(String orderId) {
        // 임시 로그인
        String userId = "2";

        CancelOrderInfo cancelOrderInfo = orderService.cancelOrderForUser(orderId, userId);

        return ApiResponse.create(OrderResponseCode.ORDER_CANCEL_SUCCESS,"주문 취소가 완료되었습니다.", cancelOrderInfo);
    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/v1/restaurants/{restaurantId}/orders/{orderId}")
    public ApiResponse<CancelOrderInfo> cancelOrderForOwner(String restaurantId, String orderId) {
        // 임시 로그인
        String userId = "2";

        CancelOrderInfo cancelOrderInfo = orderService.cancelOrderForOwner(restaurantId, orderId, userId);

        return ApiResponse.create(OrderResponseCode.ORDER_CANCEL_SUCCESS,"주문 취소가 완료되었습니다.", cancelOrderInfo);
    }
}