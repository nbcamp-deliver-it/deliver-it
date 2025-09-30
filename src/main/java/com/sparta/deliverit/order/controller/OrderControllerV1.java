package com.sparta.deliverit.order.controller;

import com.sparta.deliverit.common.dto.Result;
import com.sparta.deliverit.order.dto.response.*;
import com.sparta.deliverit.order.entity.OrderStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class OrderControllerV1 implements OrderController{

    @GetMapping("/v1/orders")
    public Result<OrderListResponse> getOrderList() {

        MenuInfo menuInfo1 = MenuInfo.builder()
                .menuName("후라이드 치킨")
                .quantity(1)
                .price(16000)
                .build();

        MenuInfo menuInfo2 = MenuInfo.builder()
                .menuName("콜라")
                .quantity(2)
                .price(6000)
                .build();

        OrderInfo orderInfo = OrderInfo.builder()
                .orderId("550e8400-e29b-41d4-a716-446655440000")
                .restaurantName("치킨성")
                .username("포이응")
                .orderTime(LocalDateTime.of(2025, 9, 30, 17, 45, 12, 345678900).toString())
                .orderStatus(OrderStatus.CREATED)
                .deliveryAddress("서울특별시 강남구 테헤란로 1927")
                .totalPrice(28000)
                .menus(List.of(menuInfo1, menuInfo2))
                .build();

        return Result.of("주문 목록을 조회했습니다.", "200", OrderListResponse.of(List.of(orderInfo)));
    }


    @GetMapping("/v1/orders/{orderId}")
    public Result<OrderResponse> getOrder(@PathVariable String orderId) {
        MenuInfo menuInfo1 = MenuInfo.builder()
                .menuName("후라이드 치킨")
                .quantity(1)
                .price(16000)
                .build();

        MenuInfo menuInfo2 = MenuInfo.builder()
                .menuName("콜라")
                .quantity(2)
                .price(6000)
                .build();

        OrderInfo orderInfo = OrderInfo.builder()
                .orderId("550e8400-e29b-41d4-a716-446655440000")
                .restaurantName("치킨성")
                .username("포이응")
                .orderTime(LocalDateTime.of(2025, 9, 30, 17, 45, 12, 345678900).toString())
                .orderStatus(OrderStatus.CREATED)
                .deliveryAddress("서울특별시 강남구 테헤란로 1927")
                .totalPrice(28000)
                .menus(List.of(menuInfo1, menuInfo2))
                .build();

        return Result.of("주문을 조회했습니다.", "200", OrderResponse.of(orderInfo));
    }
}
