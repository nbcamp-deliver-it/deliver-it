package com.sparta.deliverit.review.presentation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.deliverit.review.presentation.dto.CreateReviewRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewControllerV1.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("올바른 리뷰 생성 요청을 보내면 200 상태코드로 성공한다")
    void createReview() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest(
                "orderId",
                1L,
                BigDecimal.valueOf(4.5),
                "정말 맛있어요"

        );

        mockMvc.perform(post("/v1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").exists());
    }

    @Test
    @DisplayName("orderId 가 없다면 요청은 400 상태코드로 실패한다")
    void orderIdNull() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest(
                null,
                1L,
                BigDecimal.valueOf(4.5),
                "정말 맛있어요"

        );

        mockMvc.perform(post("/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("userId 가 없다면 요청은 400 상태코드로 실패한다")
    void userIdNull() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest(
                "orderId",
                null,
                BigDecimal.valueOf(4.5),
                "정말 맛있어요"

        );

        mockMvc.perform(post("/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("star 가 없다면 요청은 400 상태코드로 실패한다")
    void starNull() throws Exception {
        CreateReviewRequest request = new CreateReviewRequest(
                "orderId",
                1L,
                null,
                "정말 맛있어요"

        );

        mockMvc.perform(post("/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
