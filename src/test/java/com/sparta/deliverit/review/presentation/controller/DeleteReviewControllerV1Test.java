package com.sparta.deliverit.review.presentation.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewControllerV1.class)
@AutoConfigureMockMvc(addFilters = false)
class DeleteReviewControllerV1Test {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("올바른 리뷰 삭제 요청을 보내면 200 상태코드로 성공한다")
    void whenRequestIsValid_thenSuccess() throws Exception {
        mockMvc.perform(delete("/v1/reviews/{reviewId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1));
    }
}
