package com.sparta.deliverit.review.presentation.controller;

import com.sparta.deliverit.review.presentation.dto.response.ReviewListResponse;
import com.sparta.deliverit.review.presentation.dto.response.ReviewResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
public class UserReviewControllerV1 {

    @GetMapping("/{userId}/reviews")
    public ResponseEntity<ReviewListResponse> getUserReviews(
            @PathVariable String userId
    ) {
        return ResponseEntity.ok(new ReviewListResponse(
                List.of(
                        new ReviewResponse(
                                1L,
                                "userId",
                                BigDecimal.valueOf(4.5),
                                "리뷰 설명"
                        )
                )
        ));
    }
}
