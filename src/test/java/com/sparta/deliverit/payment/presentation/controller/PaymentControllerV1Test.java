//package com.sparta.deliverit.payment.presentation.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sparta.deliverit.payment.presentation.dto.PaymentRequestDto;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest
//@AutoConfigureMockMvc(addFilters = false)
//class PaymentControllerV1Test {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper mapper;
//
//    @Test
//    @DisplayName("결제가 정상적으로 성공")
//    void payment() throws Exception {
//        //given
//        final String orderId = "orderId";
//        PaymentRequestDto requestDto =
//                new PaymentRequestDto("카드", "삼성", "1234-5678-9123-1234", 1000);
//        System.out.println(mapper.writeValueAsString(requestDto));
//
//        //when
//        mockMvc.perform(post("/v1/{orderId}/payment", orderId)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(requestDto)))
//                //then
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.paymentId").isString())
//                .andExpect(jsonPath("$.orderId").value(orderId))
//                .andExpect(jsonPath("$.cardNum").value(requestDto.getCardNum()))
//                .andExpect(jsonPath("$.cardCompany").value(requestDto.getCompany()))
//                .andExpect(jsonPath("$.totalPrice").value(requestDto.getTotalPrice()))
//                .andExpect(jsonPath("$.paidAt").isNotEmpty());
//    }
//
//    @Test
//    @DisplayName("결제 정보 조회")
//    void getPayment() throws Exception {
//        //given
//        final String orderId = "orderId";
//        final String paymentId = "paymentId";
//        //when
//        mockMvc.perform(get("/v1/payment/{orderId}/{paymentId}", orderId, paymentId)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.APPLICATION_JSON))
//                //then
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.paymentId").value(paymentId))
//                .andExpect(jsonPath("$.orderId").value(orderId))
//                .andExpect(jsonPath("$.cardNum").isNotEmpty())
//                .andExpect(jsonPath("$.cardCompany").isNotEmpty())
//                .andExpect(jsonPath("$.paidAt").isNotEmpty());
//    }
//
//    @Test
//    @DisplayName("결제 정보 삭제")
//    void deletePayment() throws Exception {
//        //given
//        final String orderId = "orderId";
//        final String paymentId = "paymentId";
//        //when
//        mockMvc.perform(get("/v1/payment/{orderId}/{paymentId}", orderId, paymentId)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.APPLICATION_JSON))
//                //then
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.paymentId").value(paymentId))
//                .andExpect(jsonPath("$.orderId").value(orderId))
//                .andExpect(jsonPath("$.cardNum").isNotEmpty())
//                .andExpect(jsonPath("$.cardCompany").isNotEmpty())
//                .andExpect(jsonPath("$.paidAt").isNotEmpty());
//    }
//}