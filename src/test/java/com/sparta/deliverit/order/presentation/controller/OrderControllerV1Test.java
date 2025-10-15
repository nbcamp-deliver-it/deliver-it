package com.sparta.deliverit.order.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.deliverit.order.application.OrderService;
import com.sparta.deliverit.order.domain.entity.OrderStatus;
import com.sparta.deliverit.order.presentation.dto.request.CreateOrderRequest;
import com.sparta.deliverit.order.presentation.dto.request.OrderMenuRequest;
import com.sparta.deliverit.order.presentation.dto.response.MenuInfo;
import com.sparta.deliverit.order.presentation.dto.response.OrderInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
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
                                .orderStatus(OrderStatus.ORDER_CREATED)
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
                                .orderStatus(OrderStatus.ORDER_CREATED)
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

    @DisplayName("고객이 주문 목록 조회 API를 호출하면 주문 목록을 반환한다.")
    @Test
    void getOrderListForUserTest() throws Exception {

        LocalDateTime from = LocalDateTime.of(2025, 10, 1, 12, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 10, 9, 12, 0, 0);

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

        Mockito.when(orderService.getOrderListForUser("1", from, to, 0, 10))
                .thenReturn(
                        new PageImpl<>(
                                List.of(
                                        OrderInfo.builder()
                                                .orderId("00000000-0000-0000-0000-000000000002")
                                                .restaurantName("맛있는집")
                                                .username("이순신")
                                                .orderTime(LocalDateTime.of(2025, 10, 6, 12, 0, 0).toString())
                                                .orderStatus(OrderStatus.ORDER_CREATED)
                                                .deliveryAddress("서울시 중구 어딘가 1-1")
                                                .menus(List.of(menuInfo1, menuInfo2))
                                                .totalPrice(new BigDecimal(35000))
                                                .build(),
                                        OrderInfo.builder()
                                                .orderId("00000000-0000-0000-0000-000000000003")
                                                .restaurantName("맛있는집")
                                                .username("이순신")
                                                .orderTime(LocalDateTime.of(2025, 10, 7, 12, 0, 0).toString())
                                                .orderStatus(OrderStatus.ORDER_CREATED)
                                                .deliveryAddress("서울시 중구 어딘가 1-1")
                                                .menus(List.of(menuInfo1, menuInfo2))
                                                .totalPrice(new BigDecimal(35000))
                                                .build()
                                ),
                                PageRequest.of(0, 10),
                                2
                        )
                );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/orders")
                                .param("from", "2025-10-01T12:00:00")
                                .param("to", "2025-10-09T12:00:00")
                                .param("pageNumber", "0")
                                .param("pageSize", "10")
                                .with(SecurityMockMvcRequestPostProcessors.user("000").roles("CUSTOMER"))

                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("고객 본인의 주문 목록을 조회했습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].orderId").value("00000000-0000-0000-0000-000000000002"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].restaurantName").value("맛있는집"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].username").value("이순신"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].orderTime").value(LocalDateTime.of(2025, 10, 6, 12, 0, 0).toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].orderStatus").value("주문 생성"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].deliveryAddress").value("서울시 중구 어딘가 1-1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].menus").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].totalPrice").value(new BigDecimal(35000).toString()));
    }

    @DisplayName("고객이 주문 목록 API 조회시, from 혹은 to를 정해진 형식으로 전달하지 않으면 Bad Request 응답")
    @Test
    void getOrderListForUserWithInvalidDateFormatTest() throws Exception {

        //when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/orders")
                                .param("from", "2025-10-0112:00:00")
                                .param("to", "2025-10-09T12:00:00")
                                .param("pageNumber", "0")
                                .param("pageSize", "10")
                                .with(SecurityMockMvcRequestPostProcessors.user("000").roles("CUSTOMER"))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("INVALID_TYPE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("잘못된 요청 파라미터 타입입니다."));
    }

    @DisplayName("고객이 주문 목록 API 조회시, pageNumber의 값이 0보다 작은 경우 VALIDATION_FAILED 응답")
    @Test
    void getOrderListForUserWithInvalidPageNumberTest() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/orders")
                                .param("from", "2025-10-01T12:00:00")
                                .param("to", "2025-10-09T12:00:00")
                                .param("pageNumber", "-1")
                                .param("pageSize", "10")
                                .with(SecurityMockMvcRequestPostProcessors.user("000").roles("CUSTOMER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("페이지 숫자는 0 이상의 정수여야 합니다."));
    }

    @DisplayName("고객이 주문 목록 API 조회시, pageSize 값이 0 이하인 경우 VALIDATION_FAILED 응답")
    @Test
    void getOrderListForUserWithInvalidPageSizeTest1() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/orders")
                                .param("from", "2025-10-01T12:00:00")
                                .param("to", "2025-10-09T12:00:00")
                                .param("pageNumber", "0")
                                .param("pageSize", "0")
                                .with(SecurityMockMvcRequestPostProcessors.user("000").roles("CUSTOMER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("페이지 크기는 1 이상의 100 이하의 양수여야 합니다."));
    }

    @DisplayName("고객이 주문 목록 API 조회시, pageSize 값이 101 이상인 경우 VALIDATION_FAILED 응답")
    @Test
    void getOrderListForUserWithInvalidPageSizeTest2() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/orders")
                                .param("from", "2025-10-01T12:00:00")
                                .param("to", "2025-10-09T12:00:00")
                                .param("pageNumber", "0")
                                .param("pageSize", "101")
                                .with(SecurityMockMvcRequestPostProcessors.user("000").roles("CUSTOMER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("페이지 크기는 1 이상의 100 이하의 양수여야 합니다."));
    }

    @DisplayName("음식점 점주가 주문 목록 조회 API를 호출하면 주문 목록을 반환한다.")
    @Test
    void getOrderListForOwnerTest() throws Exception {

        LocalDateTime from = LocalDateTime.of(2025, 10, 1, 12, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 10, 9, 12, 0, 0);

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

        Mockito.when(orderService.getOrderListForOwner("1", "11111111-1111-1111-1111-111111111111", from, to, 0, 10))
                .thenReturn(
                        new PageImpl<>(
                                List.of(
                                        OrderInfo.builder()
                                                .orderId("00000000-0000-0000-0000-000000000002")
                                                .restaurantName("맛있는집")
                                                .username("이순신")
                                                .orderTime(LocalDateTime.of(2025, 10, 6, 12, 0, 0).toString())
                                                .orderStatus(OrderStatus.ORDER_CREATED)
                                                .deliveryAddress("서울시 중구 어딘가 1-1")
                                                .menus(List.of(menuInfo1, menuInfo2))
                                                .totalPrice(new BigDecimal(35000))
                                                .build(),
                                        OrderInfo.builder()
                                                .orderId("00000000-0000-0000-0000-000000000003")
                                                .restaurantName("맛있는집")
                                                .username("이순신")
                                                .orderTime(LocalDateTime.of(2025, 10, 7, 12, 0, 0).toString())
                                                .orderStatus(OrderStatus.ORDER_CREATED)
                                                .deliveryAddress("서울시 중구 어딘가 1-1")
                                                .menus(List.of(menuInfo1, menuInfo2))
                                                .totalPrice(new BigDecimal(35000))
                                                .build()
                                ),
                                PageRequest.of(0, 10),
                                2
                        )
                );

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/restaurants/{restaurantId}/orders", "11111111-1111-1111-1111-111111111111")
                                .param("from", "2025-10-01T12:00:00")
                                .param("to", "2025-10-09T12:00:00")
                                .param("pageNumber", "0")
                                .param("pageSize", "10")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))

                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("음식점에서 주문 목록을 조회했습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].orderId").value("00000000-0000-0000-0000-000000000002"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].restaurantName").value("맛있는집"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].username").value("이순신"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].orderTime").value(LocalDateTime.of(2025, 10, 6, 12, 0, 0).toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].orderStatus").value("주문 생성"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].deliveryAddress").value("서울시 중구 어딘가 1-1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].menus").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content[0].totalPrice").value(new BigDecimal(35000).toString()));
    }

    @DisplayName("음식점 점주가 주문 목록 API 조회시, from 혹은 to를 정해진 형식으로 전달하지 않으면 Bad Request 응답")
    @Test
    void getOrderListForOwnerWithInvalidDateFormatTest() throws Exception {

        //when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/restaurants/{restaurantId}/orders", "11111111-1111-1111-1111-111111111111")
                                .param("from", "2025-10-0112:00:00")
                                .param("to", "2025-10-09T12:00:00")
                                .param("pageNumber", "0")
                                .param("pageSize", "10")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))

                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("INVALID_TYPE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("잘못된 요청 파라미터 타입입니다."));
    }

    @DisplayName("음식점 점주가 주문 목록 API 조회시, pageNumber의 값이 0보다 작은 경우 VALIDATION_FAILED 응답")
    @Test
    void getOrderListForOwnerWithInvalidPageNumberTest() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/restaurants/{restaurantId}/orders", "11111111-1111-1111-1111-111111111111")
                                .param("from", "2025-10-01T12:00:00")
                                .param("to", "2025-10-09T12:00:00")
                                .param("pageNumber", "-1")
                                .param("pageSize", "10")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))

                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("페이지 숫자는 0 이상의 정수여야 합니다."));
    }

    @DisplayName("음식점 점주가 주문 목록 API 조회시, pageSize 값이 0 이하인 경우 VALIDATION_FAILED 응답")
    @Test
    void getOrderListForOwnerWithInvalidPageSizeTest1() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/restaurants/{restaurantId}/orders", "11111111-1111-1111-1111-111111111111")
                                .param("from", "2025-10-01T12:00:00")
                                .param("to", "2025-10-09T12:00:00")
                                .param("pageNumber", "0")
                                .param("pageSize", "0")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("페이지 크기는 1 이상의 100 이하의 양수여야 합니다."));
    }

    @DisplayName("음식점 점주가 주문 목록 API 조회시, pageSize 값이 101 이상인 경우 VALIDATION_FAILED 응답")
    @Test
    void getOrderListForOwnerWithInvalidPageSizeTest2() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/restaurants/{restaurantId}/orders", "11111111-1111-1111-1111-111111111111")
                                .param("from", "2025-10-01T12:00:00")
                                .param("to", "2025-10-09T12:00:00")
                                .param("pageNumber", "0")
                                .param("pageSize", "101")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("페이지 크기는 1 이상의 100 이하의 양수여야 합니다."));
    }

    @DisplayName("음식점 점주가 주문 목록 API 조회시, restaurantId가 UUID 형식이 아니면(글자 수 불일치) VALIDATION_FAILED 응답")
    @Test
    void getOrderListForOwnerWithInvalidRestaurantId1() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/restaurants/{restaurantId}/orders", "not-a-uuid")
                                .param("from", "2025-10-01T12:00:00")
                                .param("to", "2025-10-09T12:00:00")
                                .param("pageNumber", "0")
                                .param("pageSize", "50")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("음식점의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 목록 API 조회시, orderId가 UUID 형식이 아니면(허용하지 않은 문자가 들어간 경우) VALIDATION_FAILED 응답")
    @Test
    void getOrderListForOwnerWithInvalidOrderId2() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/restaurants/{restaurantId}/orders", "1f8e1d59-b0*0-4b$b-86f3-9a9c6ffb69c3")
                                .param("from", "2025-10-01T12:00:00")
                                .param("to", "2025-10-09T12:00:00")
                                .param("pageNumber", "0")
                                .param("pageSize", "50")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("음식점의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 확인 API 요청시, orderId UUID 형식이 아니면(글자 수 불일치) VALIDATION_FAILED 응답")
    @Test
    void confirmOrderForOwnerWithInvalidOrderId1() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/restaurants/{restaurantId}/orders/{orderId}/confirm", "11111111-1111-1111-1111-111111111111", "not-a-uuid")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 확인 API 요청시, OrderId UUID 형식이 아니면(허용하지 않은 문자가 들어간 경우) VALIDATION_FAILED 응답")
    @Test
    void confirmOrderForOwnerWithInvalidOrderId2() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/restaurants/{restaurantId}/orders/{orderId}/confirm", "11111111-1111-1111-1111-111111111111", "1f8e1d59-b0*0-4b$b-86f3-9a9c6ffb69c3")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 확인 API 요청시, restaurantId UUID 형식이 아니면(글자 수 불일치) VALIDATION_FAILED 응답")
    @Test
    void confirmOrderForOwnerWithInvalidRestaurantId1() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/restaurants/{restaurantId}/orders/{orderId}/confirm", "not-a-uuid", "11111111-1111-1111-1111-111111111111")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("음식점의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 확인 API 요청시, restaurantId UUID 형식이 아니면(허용하지 않은 문자가 들어간 경우) VALIDATION_FAILED 응답")
    @Test
    void confirmOrderForOwnerWithInvalidRestaurantId2() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/restaurants/{restaurantId}/orders/{orderId}/confirm", "1f8e1d59-b0*0-4b$b-86f3-9a9c6ffb69c3", "11111111-1111-1111-1111-111111111111")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("음식점의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 취소 API 요청시, orderId UUID 형식이 아니면(글자 수 불일치) VALIDATION_FAILED 응답")
    @Test
    void cancelOrderForUserWithInvalidRestaurantId1() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/v1/orders/{orderId}", "not-a-uuid")
                                .with(SecurityMockMvcRequestPostProcessors.user("000").roles("CUSTOMER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 취소 API 요청시, orderId UUID 형식이 아니면(허용하지 않은 문자가 들어간 경우) VALIDATION_FAILED 응답")
    @Test
    void cancelOrderForUserWithInvalidRestaurantId2() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/v1/orders/{orderId}", "1f8e1d59-b0*0-4b$b-86f3-9a9c6ffb69c3")
                                .with(SecurityMockMvcRequestPostProcessors.user("000").roles("CUSTOMER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 취소 API 요청시, restaurantId UUID 형식이 아니면(글자 수 불일치) VALIDATION_FAILED 응답")
    @Test
    void cancelOrderForOwnerWithInvalidRestaurantId1() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/v1/restaurants/{restaurantId}/orders/{orderId}", "not-a-uuid", "00000000-0000-0000-0000-000000000004")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("음식점의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 취소 API 요청시, restaurantId UUID 형식이 아니면(허용하지 않은 문자가 들어간 경우) VALIDATION_FAILED 응답")
    @Test
    void cancelOrderForOwnerWithInvalidRestaurantId2() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/v1/restaurants/{restaurantId}/orders/{orderId}", "11111111-1111-1111-1111-11111**11111", "00000000-0000-0000-0000-000000000004")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("음식점의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 취소 API 요청시, orderId UUID 형식이 아니면(글자 수 불일치) VALIDATION_FAILED 응답")
    @Test
    void cancelOrderForOwnerWithOrderId1() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/v1/restaurants/{restaurantId}/orders/{orderId}", "11111111-1111-1111-1111-111111111111", "not-a-uuid")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("음식점 점주가 주문 취소 API 요청시, orderId UUID 형식이 아니면(허용하지 않은 문자가 들어간 경우) VALIDATION_FAILED 응답")
    @Test
    void cancelOrderForOwnerWithOrderId2() throws Exception {

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/v1/restaurants/{restaurantId}/orders/{orderId}", "11111111-1111-1111-1111-111111111111", "1f8e1d59-b0*0-4b$b-86f3-9a9c6ffb69c3" )
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("주문 생성 시 RestaurantId가 UUID 형식이 아니면(글자 수 불일치) VALIDATION_FAILED 응답")
    @Test
    void createOrderWithInvalidRestaurantId1() throws Exception {
        OrderMenuRequest menuRequest1 = OrderMenuRequest.builder()
                .menuId("2d9a92a1-69e0-4d12-8032-2ac6a1a7e501")
                .quantity(2)
                .build();

        OrderMenuRequest menuRequest2 = OrderMenuRequest.builder()
                .menuId("e0a476d8-3f29-4b32-b021-d89a447d2f7f")
                .quantity(1)
                .build();

        List<OrderMenuRequest> orderMenuRequests = List.of(menuRequest1, menuRequest2);
        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("not-a-uuid")
                .menus(orderMenuRequests)
                .deliveryAddress("서울특별시 강남구 테헤란로 1927")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("음식점의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("주문 생성 시 RestaurantId가 UUID 형식이 아니면(특수문자 포함) VALIDATION_FAILED 응답")
    @Test
    void createOrderWithInvalidRestaurantId2() throws Exception {
        OrderMenuRequest menuRequest1 = OrderMenuRequest.builder()
                .menuId("2d9a92a1-69e0-4d12-8032-2ac6a1a7e501")
                .quantity(2)
                .build();

        OrderMenuRequest menuRequest2 = OrderMenuRequest.builder()
                .menuId("e0a476d8-3f29-4b32-b021-d89a447d2f7f")
                .quantity(1)
                .build();

        List<OrderMenuRequest> orderMenuRequests = List.of(menuRequest1, menuRequest2);
        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("1f8e1d59-b0*0-4b$b-86f3-9a9c6ffb69c3")
                .menus(orderMenuRequests)
                .deliveryAddress("서울특별시 강남구 테헤란로 1927")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("음식점의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("주문 생성 시 RestaurantId가 비어 있으면 VALIDATION_FAILED 응답")
    @Test
    void createOrderWithInvalidRestaurantId3() throws Exception {
        OrderMenuRequest menuRequest1 = OrderMenuRequest.builder()
                .menuId("2d9a92a1-69e0-4d12-8032-2ac6a1a7e501")
                .quantity(2)
                .build();

        OrderMenuRequest menuRequest2 = OrderMenuRequest.builder()
                .menuId("e0a476d8-3f29-4b32-b021-d89a447d2f7f")
                .quantity(1)
                .build();

        List<OrderMenuRequest> orderMenuRequests = List.of(menuRequest1, menuRequest2);
        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("")
                .menus(orderMenuRequests)
                .deliveryAddress("서울특별시 강남구 테헤란로 1927")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").isString())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").isString());
    }

    @DisplayName("주문 생성 시 RestaurantId가 UUID 형식에 맞으면 정상 응답을 반환한다.")
    @Test
    void createOrderWitValidRestaurantId() throws Exception {
        OrderMenuRequest menuRequest1 = OrderMenuRequest.builder()
                .menuId("2d9a92a1-69e0-4d12-8032-2ac6a1a7e501")
                .quantity(2)
                .build();

        OrderMenuRequest menuRequest2 = OrderMenuRequest.builder()
                .menuId("e0a476d8-3f29-4b32-b021-d89a447d2f7f")
                .quantity(1)
                .build();

        List<OrderMenuRequest> orderMenuRequests = List.of(menuRequest1, menuRequest2);
        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("1f8e1d59-b000-4b0b-86f3-9a9c6ffb69c3")
                .menus(orderMenuRequests)
                .deliveryAddress("서울특별시 강남구 테헤란로 1927")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_CREATE_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문을 정상적으로 생성했습니다."));
    }

    @DisplayName("주문 생성 시 menu가 0개이면 VALIDATION_FAILED 응답")
    @Test
    void createOrderWithMenusEmpty() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("1f8e1d59-b000-4b0b-86f3-9a9c6ffb69c3")
                .menus(List.of())
                .deliveryAddress("서울특별시 강남구 테헤란로 1927")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("메뉴는 적어도 한 개 이상 주문해야합니다. "));
    }

    @DisplayName("주문 생성 시 menu가 1개 이상이면 정상 응답을 반환한다.")
    @Test
    void createOrderWithMenuNotEmpty() throws Exception {
        OrderMenuRequest menuRequest1 = OrderMenuRequest.builder()
                .menuId("2d9a92a1-69e0-4d12-8032-2ac6a1a7e501")
                .quantity(2)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("1f8e1d59-b000-4b0b-86f3-9a9c6ffb69c3")
                .menus(List.of(menuRequest1))
                .deliveryAddress("서울특별시 강남구 테헤란로 1927")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_CREATE_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문을 정상적으로 생성했습니다."));
    }

    @DisplayName("주문 생성 시 주소가 비어 있으면 VALIDATION_FAILED 응답")
    @Test
    void createOrderWithAddressEmpty() throws Exception {
        OrderMenuRequest menuRequest1 = OrderMenuRequest.builder()
                .menuId("2d9a92a1-69e0-4d12-8032-2ac6a1a7e501")
                .quantity(2)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("1f8e1d59-b000-4b0b-86f3-9a9c6ffb69c3")
                .menus(List.of(menuRequest1))
                .deliveryAddress("")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("배송지는 필수 값입니다."));
    }

    @DisplayName("주문 생성 시 주소가 입력되어 있으면 정상 응답을 반환한다.")
    @Test
    void createOrderWithAddressNotEmpty() throws Exception {
        OrderMenuRequest menuRequest1 = OrderMenuRequest.builder()
                .menuId("2d9a92a1-69e0-4d12-8032-2ac6a1a7e501")
                .quantity(2)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("1f8e1d59-b000-4b0b-86f3-9a9c6ffb69c3")
                .menus(List.of(menuRequest1))
                .deliveryAddress("경기도 수원시 영통구 영통로")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_CREATE_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문을 정상적으로 생성했습니다."));
    }

    @DisplayName("주문 생성 시 MenuId가 UUID 형식이 아니면(글자 수 불일치) VALIDATION_FAILED 응답")
    @Test
    void createOrderWithInvalidMenuId1() throws Exception {
        OrderMenuRequest menuRequest1 = OrderMenuRequest.builder()
                .menuId("not-a-uuid")
                .quantity(1)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("1f8e1d59-b000-4b0b-86f3-9a9c6ffb69c3")
                .menus(List.of(menuRequest1))
                .deliveryAddress("경기도 수원시 영통구 영통로")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("메뉴의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("주문 생성 시 MenuId가 UUID 형식이 아니면(특수문자 포함) VALIDATION_FAILED 응답")
    @Test
    void createOrderWithInvalidMenuId2() throws Exception {
        OrderMenuRequest menuRequest1 = OrderMenuRequest.builder()
                .menuId("e0a476d8-3f29-4**2-b021-d89a447d2f7f")
                .quantity(1)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("1f8e1d59-b000-4b0b-86f3-9a9c6ffb69c3")
                .menus(List.of(menuRequest1))
                .deliveryAddress("경기도 수원시 영통구 영통로")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("메뉴의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("주문 생성 시 MenuId가 비어 있으면 VALIDATION_FAILED 응답")
    @Test
    void createOrderWithInvalidMenuId3() throws Exception {
        OrderMenuRequest menuRequest1 = OrderMenuRequest.builder()
                .menuId("")
                .quantity(1)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("1f8e1d59-b000-4b0b-86f3-9a9c6ffb69c3")
                .menus(List.of(menuRequest1))
                .deliveryAddress("경기도 수원시 영통구 영통로")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("메뉴의 UUID 값은 필수입니다."));
    }

    @DisplayName("주문 생성 시 MenuId가 UUID 형식에 맞으면 정상 응답을 반환한다.")
    @Test
    void createOrderWithValidMenuId() throws Exception {
        OrderMenuRequest menuRequest1 = OrderMenuRequest.builder()
                .menuId("e0a476d8-3f29-4b32-b021-d89a447d2f7f")
                .quantity(1)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("1f8e1d59-b000-4b0b-86f3-9a9c6ffb69c3")
                .menus(List.of(menuRequest1))
                .deliveryAddress("경기도 수원시 영통구 영통로")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_CREATE_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문을 정상적으로 생성했습니다."));
    }

    @DisplayName("주문 생성 시 메뉴의 수량이 자연수가 아니면(0 이하) VALIDATION_FAILED 응답")
    @Test
    void createOrderWithMenuQuantityIsZero() throws Exception {
        OrderMenuRequest menuRequest1 = OrderMenuRequest.builder()
                .menuId("e0a476d8-3f29-4b32-b021-d89a447d2f7f")
                .quantity(0)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("1f8e1d59-b000-4b0b-86f3-9a9c6ffb69c3")
                .menus(List.of(menuRequest1))
                .deliveryAddress("경기도 수원시 영통구 영통로")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("메뉴 수량은 양의 정수여야 합니다."));
    }

    @DisplayName("주문 생성 시 메뉴의 수량이 자연수라면 정상 응답을 반환한다.")
    @Test
    void createOrderWithMenuQuantityIsNaturalNumber() throws Exception {
        OrderMenuRequest menuRequest1 = OrderMenuRequest.builder()
                .menuId("e0a476d8-3f29-4b32-b021-d89a447d2f7f")
                .quantity(1)
                .build();

        CreateOrderRequest request = CreateOrderRequest.builder()
                .restaurantId("1f8e1d59-b000-4b0b-86f3-9a9c6ffb69c3")
                .menus(List.of(menuRequest1))
                .deliveryAddress("경기도 수원시 영통구 영통로")
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders")
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_CREATE_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문을 정상적으로 생성했습니다."));
    }
}