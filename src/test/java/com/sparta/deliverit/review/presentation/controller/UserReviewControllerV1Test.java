package com.sparta.deliverit.review.presentation.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserReviewControllerV1.class)
@AutoConfigureMockMvc(addFilters = false)
class UserReviewControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("유저 리뷰 조회")
    class GetUserReview {
        @Test
        @DisplayName("유저 리뷰 조회 요청이 유효하면 200 상태코드로 주문 리스트를 반환한다")
        void whenRequestIsValid_thenSuccess() throws Exception {
            mockMvc.perform(get("/v1/users/userId/reviews")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.list").isArray())
                    .andExpect(jsonPath("$.list[0].reviewId").isNumber())
                    .andExpect(jsonPath("$.list[0].userId").isString())
                    .andExpect(jsonPath("$.list[0].star").isNumber())
                    .andExpect(jsonPath("$.list[0].description").isString());
        }
    }
}
