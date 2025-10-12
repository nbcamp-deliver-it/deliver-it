package com.sparta.deliverit.order.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.deliverit.order.application.OrderService;
import com.sparta.deliverit.order.domain.entity.OrderStatus;
import com.sparta.deliverit.order.presentation.dto.response.MenuInfo;
import com.sparta.deliverit.order.presentation.dto.response.OrderInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@WebMvcTest(controllers = OrderControllerV1.class)
class OrderControllerV1Test {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    OrderService orderService;

    @DisplayName("고객이 주문 조회 API를 호출하면 주문 목록을 반환한다.")
    @Test
    void getOrderDetailForUser() throws Exception {

        LocalDateTime from = LocalDateTime.of(2025, 10, 1, 12, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 10, 8, 12, 0, 0);


        MenuInfo menuInfo1 = MenuInfo.builder()
                .menuName("짜장면")
                .price(new BigDecimal(7000))
                .quantity(2)
                .build();

        MenuInfo menuInfo2 = MenuInfo.builder()
                .menuName("탕수육")
                .price(new BigDecimal(21000))
                .quantity(1)
                .build();

        Mockito.when(orderService.getOrderDetailForUser("00000000-0000-0000-0000-000000000002", "1"))
                .thenReturn(
                        OrderInfo.builder()
                                .orderId("00000000-0000-0000-0000-000000000002")
                                .restaurantName("맛있는집")
                                .username("이순신")
                                .orderTime(LocalDateTime.of(2025, 10, 8, 12, 0, 0).toString())
                                .orderStatus(OrderStatus.CREATED)
                                .deliveryAddress("서울시 중구 어딘가 1-1")
                                .menus(List.of(menuInfo1, menuInfo2))
                                .totalPrice(new BigDecimal(35000))
                                .build()
                );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/orders/{orderId}", "00000000-0000-0000-0000-000000000002")
                                .with(SecurityMockMvcRequestPostProcessors.user("OOO").roles("CUSTOMER"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("고객의 주문을 조회했습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderId").value("00000000-0000-0000-0000-000000000002"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.restaurantName").value("맛있는집"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("이순신"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderTime").value(LocalDateTime.of(2025, 10, 8, 12, 0, 0).toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("주문 생성"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.deliveryAddress").value("서울시 중구 어딘가 1-1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.menus").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalPrice").value(new BigDecimal(35000).toString()));
    }

    @DisplayName("고객이 주문 API 조회시, orderId가 UUID 형식이 아니면(글자 수 불일치) VALIDATION_FAILED 응답")
    @Test
    void getOrderDetailForUserWithInvalidOrderId1() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/orders/{orderId}", "not-a-uuid")
                                .with(SecurityMockMvcRequestPostProcessors.user("000").roles("CUSTOMER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("고객이 주문 API 조회시, orderId가 UUID 형식이 아니면(허용하지 않은 문자가 들어간 경우) VALIDATION_FAILED 응답")
    @Test
    void getOrderDetailForUserWithInvalidOrderId2() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/orders/{orderId}", "1f8e1d59-b0*0-4b$b-86f3-9a9c6ffb69c3")
                                .with(SecurityMockMvcRequestPostProcessors.user("000").roles("CUSTOMER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 조회 API를 호출하면 주문 목록을 반환한다.")
    @Test
    void getOrderForOwnerTest() throws Exception {

        MenuInfo menuInfo1 = MenuInfo.builder()
                .menuName("짜장면")
                .price(new BigDecimal(7000))
                .quantity(2)
                .build();

        MenuInfo menuInfo2 = MenuInfo.builder()
                .menuName("탕수육")
                .price(new BigDecimal(21000))
                .quantity(1)
                .build();

        Mockito.when(orderService.getOrderDetailForOwner("00000000-0000-0000-0000-000000000002", "1"))
                .thenReturn(
                        OrderInfo.builder()
                                .orderId("00000000-0000-0000-0000-000000000002")
                                .restaurantName("맛있는집")
                                .username("이순신")
                                .orderTime(LocalDateTime.of(2025, 10, 8, 12, 0, 0).toString())
                                .orderStatus(OrderStatus.CREATED)
                                .deliveryAddress("서울시 중구 어딘가 1-1")
                                .menus(List.of(menuInfo1, menuInfo2))
                                .totalPrice(new BigDecimal(35000))
                                .build()
                );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/restaurants/{restaurantId}/orders/{orderId}", "11111111-1111-1111-1111-111111111111", "00000000-0000-0000-0000-000000000002")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("음식점이 주문을 조회했습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderId").value("00000000-0000-0000-0000-000000000002"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.restaurantName").value("맛있는집"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("이순신"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderTime").value(LocalDateTime.of(2025, 10, 8, 12, 0, 0).toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("주문 생성"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.deliveryAddress").value("서울시 중구 어딘가 1-1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.menus").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalPrice").value(new BigDecimal(35000).toString()));
    }

    @DisplayName("음식점 점주가 주문 API 조회시, restaurantId가 UUID 형식이 아니면(글자 수 불일치) VALIDATION_FAILED 응답")
    @Test
    void getOrderDetailForOwnerWithInvalidRestaurantId1() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/restaurants/{restaurantId}/orders/{orderId}", "not-a-uuid", "00000000-0000-0000-0000-000000000002")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("음식점의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 API 조회시, restaurantId가 UUID 형식이 아니면(허용하지 않은 문자가 들어간 경우) VALIDATION_FAILED 응답")
    @Test
    void getOrderDetailForOwnerWithInvalidRestaurantId2() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/restaurants/{restaurantId}/orders/{orderId}", "1f8e1d59-b0*0-4b$b-86f3-9a9c6ffb69c3", "00000000-0000-0000-0000-000000000002")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("음식점의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 API 조회시, orderId UUID 형식이 아니면(글자 수 불일치) VALIDATION_FAILED 응답")
    @Test
    void getOrderDetailForOwnerWithInvalidOrderId1() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/restaurants/{restaurantId}/orders/{orderId}", "11111111-1111-1111-1111-111111111111", "not-a-uuid")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 API 조회시, OrderId UUID 형식이 아니면(허용하지 않은 문자가 들어간 경우) VALIDATION_FAILED 응답")
    @Test
    void getOrderDetailForOwnerWithInvalidOrderId2() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/restaurants/{restaurantId}/orders/{orderId}", "11111111-1111-1111-1111-111111111111", "1f8e1d59-b0*0-4b$b-86f3-9a9c6ffb69c3")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }
}