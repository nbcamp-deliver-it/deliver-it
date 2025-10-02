package com.sparta.deliverit.order.presentation.controller;

import com.sparta.deliverit.global.presentation.dto.Result;
import com.sparta.deliverit.order.presentation.dto.response.CancelOrderInfo;
import com.sparta.deliverit.order.presentation.dto.request.CreateOrderRequest;
import com.sparta.deliverit.order.domain.entity.OrderStatus;
import com.sparta.deliverit.order.presentation.dto.response.ConfirmOrderInfo;
import com.sparta.deliverit.order.presentation.dto.response.CreateOrderInfo;
import com.sparta.deliverit.order.presentation.dto.response.MenuInfo;
import com.sparta.deliverit.order.presentation.dto.response.OrderInfo;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
public class OrderControllerV1 implements OrderController {

    @GetMapping("/v1/orders")
    public Result<List<OrderInfo>> getOrderList(Authentication userAuthInfo) {

        if (isCustomer(userAuthInfo)) {
            // TODO: 고객 정보를 이용해서 주문 목록을 가져오도록 구현해야 함
            MenuInfo menuInfo1 = MenuInfo.builder()
                    .menuName("간짜장")
                    .quantity(2)
                    .price(7000)
                    .build();

            MenuInfo menuInfo2 = MenuInfo.builder()
                    .menuName("탕수육")
                    .quantity(1)
                    .price(15000)
                    .build();

            OrderInfo orderInfo = OrderInfo.builder()
                    .orderId("7939146e-b329-4f6e-9fa9-673381e78b8a")
                    .restaurantName("짜왕")
                    .username("두둥탁")
                    .orderTime(LocalDateTime.of(2025, 3, 3, 11, 45, 11, 345128900).toString())
                    .orderStatus(OrderStatus.CREATED)
                    .deliveryAddress("경기도 수원시 영통구 영통로")
                    .totalPrice(29000)
                    .menus(List.of(menuInfo1, menuInfo2))
                    .build();

            return Result.of("주문 목록을 조회했습니다.", "200", List.of(orderInfo));
        } else if (isOwner(userAuthInfo)) {
            // TODO: 가능하면 API를 분리해주는 것이 좋을 것 같음
            // TODO: 레스토랑 정보를 바탕으로 주문 목록을 가져오도록 구현해야함
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

            return Result.of("주문 목록을 조회했습니다.", "200", List.of(orderInfo));
        }

        throw new AccessDeniedException("권한이 없습니다.");
    }

    @GetMapping("/v1/orders/{orderId}")
    public Result<OrderInfo> getOrder(@PathVariable String orderId, Authentication userAuthInfo) {

        if (isCustomer(userAuthInfo)) {
            MenuInfo menuInfo1 = MenuInfo.builder()
                    .menuName("간짜장")
                    .quantity(2)
                    .price(7000)
                    .build();

            MenuInfo menuInfo2 = MenuInfo.builder()
                    .menuName("탕수육")
                    .quantity(1)
                    .price(15000)
                    .build();

            OrderInfo orderInfo = OrderInfo.builder()
                    .orderId("7939146e-b329-4f6e-9fa9-673381e78b8a")
                    .restaurantName("짜왕")
                    .username("두둥탁")
                    .orderTime(LocalDateTime.of(2025, 3, 3, 11, 45, 11, 345128900).toString())
                    .orderStatus(OrderStatus.CREATED)
                    .deliveryAddress("경기도 수원시 영통구 영통로")
                    .totalPrice(29000)
                    .menus(List.of(menuInfo1, menuInfo2))
                    .build();

            return Result.of("주문을 조회했습니다.", "200", orderInfo);

        } else if (isOwner(userAuthInfo)) {
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

            return Result.of("주문을 조회했습니다.", "200", orderInfo);

        }

        throw new AccessDeniedException("권한이 없습니다.");
    }

    @PostMapping("/v1/orders")
    public Result<CreateOrderInfo> createOrder(CreateOrderRequest request) {
        CreateOrderInfo orderInfo = CreateOrderInfo.builder()
                .orderId("7939146e-b329-4f6e-9fa9-673381e78b8a")
                .orderStatus("PENDING_PAYMENT")
                .totalPrice(28000)
                .build();

        return Result.of("주문이 정상적으로 완료되었습니다.", "201", orderInfo);
    }

    @PostMapping("/v1/orders/{orderId}/confirm")
    public Result<ConfirmOrderInfo> confirmOrder(@PathVariable String orderId) {

        ConfirmOrderInfo confirmOrderInfo = ConfirmOrderInfo.of("550e8400-e29b-41d4-a716-446655440000", OrderStatus.CONFIRMED.getDescription(), "2025-09-29T20:15:42+09:00");

        return Result.of("주문 확인이 완료되었습니다.", "200", confirmOrderInfo);
    }

    @PatchMapping("/v1/orders/{orderId}")
    public Result<CancelOrderInfo> cancelOrder(@PathVariable String orderId) {

        CancelOrderInfo cancelOrderInfo = CancelOrderInfo.of("550e8400-e29b-41d4-a716-446655440000", OrderStatus.CONFIRMED.getDescription(), OrderStatus.CANCELED.getDescription(), "2025-09-29T20:25:05+09:00");

        return Result.of("주문 취소가 완료되었습니다.", "200", cancelOrderInfo);
    }

    private static boolean isOwner(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_OWNER"));
    }

    private static boolean isCustomer(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));
    }
}
