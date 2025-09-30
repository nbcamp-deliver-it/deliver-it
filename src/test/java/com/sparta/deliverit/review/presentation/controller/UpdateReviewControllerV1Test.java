package com.sparta.deliverit.review.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.deliverit.review.presentation.dto.request.UpdateReviewRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewControllerV1.class)
@AutoConfigureMockMvc(addFilters = false)
class UpdateReviewControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("올바른 리뷰 수정 요청을 보내면 200 상태코드로 성공한다")
    void whenRequestIsValid_thenSuccess() throws Exception {
         var request = new UpdateReviewRequest(
                BigDecimal.valueOf(4.5),
                "정말 맛있어요"

        );

        mockMvc.perform(put("/v1/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").exists());
    }

    @Test
    @DisplayName("star 가 없다면 요청은 400 상태코드로 실패한다")
    void whenStarIsNull_thenFail() throws Exception {
        var request = new UpdateReviewRequest(
                null,
                "정말 맛있어요"

        );

        mockMvc.perform(put("/v1/reviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Nested
    @DisplayName("값 형식과 범위 검증")
    class ValueRoleTest {

        @Test
        @DisplayName("reviewId 가 음수 혹은 0이라면 요청은 400 상태코드로 실패한다")
        void whenReviewIdIsNegative_thenFail() throws Exception {
            var request = new UpdateReviewRequest(
                    BigDecimal.valueOf(4.5),
                    "정말 맛있어요"

            );

            mockMvc.perform(put("/v1/reviews/{reviewId}", -1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            mockMvc.perform(put("/v1/reviews/{reviewId}", 0)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("star 의 값이 1.0 이상이 아니라면 요청은 400 상태코드로 실패한다")
        void whenStarIsLessThenOne_thenFail() throws Exception {
            var request = new UpdateReviewRequest(
                    BigDecimal.valueOf(0.9),
                    null
            );

            mockMvc.perform(put("/v1/reviews/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("star 의 값이 5.0 이하가 아니라면 요청은 400 상태코드로 실패한다")
        void whenStarIsGreaterThenFive_thenFail() throws Exception {
            var request = new UpdateReviewRequest(
                    BigDecimal.valueOf(5.1),
                    null
            );

            mockMvc.perform(put("/v1/reviews/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
