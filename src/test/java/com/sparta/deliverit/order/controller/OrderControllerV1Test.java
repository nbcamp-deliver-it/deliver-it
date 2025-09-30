package com.sparta.deliverit.order.controller;

import com.sparta.deliverit.order.entity.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = OrderControllerV1.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerV1Test {

    @Autowired
    MockMvc mockMvc;
    
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

}