package com.sparta.deliverit.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.deliverit.order.presentation.controller.OrderControllerV1;
import com.sparta.deliverit.order.presentation.dto.request.CreateOrderRequest;
import com.sparta.deliverit.order.presentation.dto.request.OrderMenuRequest;
import com.sparta.deliverit.order.domain.entity.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(controllers = OrderControllerV1.class)
@AutoConfigureMockMvc()
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
                                .with(SecurityMockMvcRequestPostProcessors.user("OOO").roles("OWNER"))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("음식점의 주문 목록을 조회했습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_LIST_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderId").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].restaurantName").value("치킨성"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].username").value("포이응"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderTime").value("2025-09-30T17:45:12.345678900"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderStatus").value(OrderStatus.CREATED.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].deliveryAddress").value("서울특별시 강남구 테헤란로 1927"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].totalPrice").value(28000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].menus").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].menus[0].menuName").value("후라이드 치킨"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].menus[0].quantity").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].menus[0].price").value(16000));
    }

    @DisplayName("주문 목록을 조회하는 사용자의 권한이 CUSTOMER일 때, 사용자가 주문 리스트를 반환한다.")
    @Test
    void getCustomerOrderListTest() throws Exception {
        // given

        // when // then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/orders")
                        .with(SecurityMockMvcRequestPostProcessors.user("홍길동").roles("CUSTOMER")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderId").value("7939146e-b329-4f6e-9fa9-673381e78b8a"));
    }

    @DisplayName("주문 목록을 조회하는 사용자의 권한이 OWNER일 때, 가게의 주문 리스트를 반환한다.")
    @Test
    void getRestaurantOrderListTest() throws Exception {
        // when // then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/orders")
                        .with(SecurityMockMvcRequestPostProcessors.user("김철수").roles("OWNER")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].orderId").value("550e8400-e29b-41d4-a716-446655440000"));
    }

    @DisplayName("주문 목록을 조회하는 사용자의 권한이 MASTER일 때, UNAUTHORIZED_USER 응답")
    @Test
    void getOrderListByMasterTest() throws Exception {
        // when // then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/orders")
                        .with(SecurityMockMvcRequestPostProcessors.user("김철수").roles("MASTER")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("UNAUTHORIZED_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문에 접근할 권한이 없습니다."));
    }

    @DisplayName("사용자가 주문 조회 API를 호출하면 주문 정보를 반환한다.")
    @Test
    void getOrderTest() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/orders/{orderId}", "550e8400-e29b-41d4-a716-446655440000")
                                .with(SecurityMockMvcRequestPostProcessors.user("OOO").roles("CUSTOMER"))

                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("고객의 주문을 조회했습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_DETAIL_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderId").value("7939146e-b329-4f6e-9fa9-673381e78b8a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.restaurantName").value("짜왕"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.username").value("두둥탁"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderTime").value("2025-03-03T11:45:11.345128900"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value(OrderStatus.CREATED.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.deliveryAddress").value("경기도 수원시 영통구 영통로"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalPrice").value(29000))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.menus").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.menus[1].menuName").value("탕수육"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.menus[1].quantity").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.menus[1].price").value(15000));

    }

    @DisplayName("주문을 조회하는 사용자의 권한이 CUSTOMER일 때, 본인의 주문인 경우만 조회 가능하다.")
    @Test
    void getCustomerOrderTest() throws Exception {
        // when // then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/orders/{orderId}", "7939146e-b329-4f6e-9fa9-673381e78b8a")
                        .with(SecurityMockMvcRequestPostProcessors.user("두둥탁").roles("CUSTOMER")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderId").value("7939146e-b329-4f6e-9fa9-673381e78b8a"));
    }

    @DisplayName("주문을 조회하는 사용자의 권한이 OWNER일 때, 레스토랑의 주문만 조회 가능하다.")
    @Test
    void getRestaurantOrderTest() throws Exception {
        // when // then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/orders/{orderId}", "550e8400-e29b-41d4-a716-446655440000")
                        .with(SecurityMockMvcRequestPostProcessors.user("포이응").roles("OWNER")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderId").value("550e8400-e29b-41d4-a716-446655440000"));
    }

    @DisplayName("주문을 조회하는 사용자의 권한이 MASTER일 때, UNAUTHORIZED_USER 응답")
    @Test
    void getOrderByMasterTest() throws Exception {
        // when // then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/orders/{orderId}", "550e8400-e29b-41d4-a716-446655440000")
                        .with(SecurityMockMvcRequestPostProcessors.user("배달의신").roles("MASTER")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("UNAUTHORIZED_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문에 접근할 권한이 없습니다."));
    }

    @DisplayName("주문의 UUID가 형식에 맞으면 정상적으로 진행된다.")
    @Test
    void getOrderWithOrderId() throws Exception {
        // when // then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/v1/orders/{orderId}", "550e8400-e29b-41d4-a716-446655440000")
                        .with(SecurityMockMvcRequestPostProcessors.user("배달의신").roles("OWNER")))
                .andExpect(MockMvcResultMatchers.status().isOk());
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
                                .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문이 정상적으로 완료되었습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderId").value("7939146e-b329-4f6e-9fa9-673381e78b8a"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("PENDING_PAYMENT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.totalPrice").value(28000));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문이 정상적으로 완료되었습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("PENDING_PAYMENT"));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문이 정상적으로 완료되었습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("PENDING_PAYMENT"));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문이 정상적으로 완료되었습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("PENDING_PAYMENT"));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("메뉴의 UUID 형식이 올바르지 않습니다."));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문이 정상적으로 완료되었습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("PENDING_PAYMENT"));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문이 정상적으로 완료되었습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("PENDING_PAYMENT"));
    }

    @DisplayName("점주가 주문 확정 API를 호출하면 주문의 상태가 주문 확인으로 변경된다.")
    @Test
    void confirmOrderTest() throws Exception {
        // given

        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/orders/{orderId}/confirm", "550e8400-e29b-41d4-a716-446655440000")
                                .with(SecurityMockMvcRequestPostProcessors.user("배달의신").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())

                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문 확인이 완료되었습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_CONFIRM_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderId").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("주문 확인"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.confirmedAt").value("2025-09-29T20:15:42+09:00"));
    }

    @DisplayName("주문 확인 시 orderId가 UUID 형식이 아니면(글자 수 불일치) VALIDATION_FAILED 응답")
    @Test
    void confirmOrderWithInvalidOrderId1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/orders/{orderId}/confirm", "not-a-uuid")
                        .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("주문 확인 시 orderId가 UUID 형식이 아니면(특수문자 포함) VALIDATION_FAILED 응답")
    @Test
    void confirmOrderWithInvalidOrderId2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/orders/{orderId}/confirm", "550e8400-e29b-41d4-a716-446655440**0")
                        .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("주문 확인 시 orderId가 UUID 형식에 맞으면 정상 응답을 반환한다.")
    @Test
    void confirmOrderWithValidOrderId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/v1/orders/{orderId}/confirm", "550e8400-e29b-41d4-a716-446655440000")
                        .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_CONFIRM_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문 확인이 완료되었습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderStatus").value("주문 확인"));
    }

    @DisplayName("고객 혹은 점주가 주문 취소 API를 호출하면 주문이 취소된다.")
    @Test
    void cancelOrderTest() throws Exception {
        // when // then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch("/v1/orders/{orderId}", "550e8400-e29b-41d4-a716-446655440000")
                                .with(SecurityMockMvcRequestPostProcessors.user("배달의신").roles("OWNER"))
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문 취소가 완료되었습니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_CANCEL_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.orderId").value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.previousStatus").value("주문 확인"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.currentStatus").value("주문 취소"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.cancelAt").value("2025-09-29T20:25:05+09:00"));
    }

    @DisplayName("주문 취소 시 orderId가 UUID 형식이 아니면(글자 수 불일치) VALIDATION_FAILED 응답")
    @Test
    void cancelOrderWithInvalidOrderId1() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/v1/orders/{orderId}", "not-a-uuid")
                        .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("주문 취소 시 orderId가 UUID 형식이 아니면(특수문자 포함) VALIDATION_FAILED 응답")
    @Test
    void cancelOrderWithInvalidOrderId2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/v1/orders/{orderId}", "550e8400-e29b-41d4-a716-446655440**0")
                        .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("VALIDATION_FAILED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문의 UUID 형식이 올바르지 않습니다."));
    }

    @DisplayName("주문 취소 시 orderId가 UUID 형식에 맞으면 정상 응답을 반환한다.")
    @Test
    void cancelOrderWithValidOrderId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/v1/orders/{orderId}", "550e8400-e29b-41d4-a716-446655440000")
                        .with(SecurityMockMvcRequestPostProcessors.user("owner").roles("OWNER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode").value("ORDER_CANCEL_SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("주문 취소가 완료되었습니다."));
    }
}