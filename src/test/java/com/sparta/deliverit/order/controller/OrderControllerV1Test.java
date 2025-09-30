package com.sparta.deliverit.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.deliverit.order.dto.request.CreateOrderRequest;
import com.sparta.deliverit.order.dto.request.OrderMenuRequest;
import com.sparta.deliverit.order.entity.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(controllers = OrderControllerV1.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerV1Test {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("사용자가 주문 목록 조회 API를 호출하면 주문 목록 정보를 반환한다.")
    @Test
    void getOrderListTest() throws Exception {

        // when then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/orders")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문 목록을 조회했습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("200"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orders").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orders[0].order_id").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orders[0].restaurant_name").value("치킨성"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orders[0].username").value("포이응"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orders[0].order_time").value("2025-09-30T17:45:12.345678900"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orders[0].order_status").value(OrderStatus.CREATED.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orders[0].delivery_address").value("서울특별시 강남구 테헤란로 1927"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orders[0].total_price").value(28000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orders[0].menus").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orders[0].menus[0].menu_name").value("후라이드 치킨"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orders[0].menus[0].quantity").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orders[0].menus[0].price").value(16000));
    }

    @DisplayName("사용자가 주문 조회 API를 호출하면 주문 정보를 반환한다.")
    @Test
    void getOrderTest() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/orders/{orderId}", "550e8400-e29b-41d4-a716-446655440000")

                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문을 조회했습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order.order_id").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order.restaurant_name").value("치킨성"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order.username").value("포이응"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order.order_time").value("2025-09-30T17:45:12.345678900"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order.order_status").value(OrderStatus.CREATED.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order.delivery_address").value("서울특별시 강남구 테헤란로 1927"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order.total_price").value(28000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order.menus").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order.menus[1].menu_name").value("콜라"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order.menus[1].quantity").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order.menus[1].price").value(6000));

    }

    @DisplayName("사용자가 주문 생성 API를 호출하면 주문이 생성되고 결과를 반환합니다.")
    @Test
    void createOrderTest() throws Exception {
        // given
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
                .restaurantId("1f8e1d59-b080-4bcb-86f3-9a9c6ffb69c3")
                .menus(orderMenuRequests)
                .deliveryAddress("서울특별시 강남구 테헤란로 1927")
                .build();


        // when // then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/v1/orders")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문이 정상적으로 완료되었습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("201"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order_info.order_id").value("7939146e-b329-4f6e-9fa9-673381e78b8a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order_info.order_status").value("PENDING_PAYMENT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.order_info.total_price").value(28000));
    }
}