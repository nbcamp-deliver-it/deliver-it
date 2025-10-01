package com.sparta.deliverit.review.presentation.dto.response;

import com.sparta.deliverit.review.application.service.dto.OrderReviewInfo;

import java.math.BigDecimal;

public record ReviewResponse(
        Long reviewId,
        Long userId,
        String userName,
        BigDecimal star,
        String description
) {
    public static ReviewResponse from(OrderReviewInfo review) {
        return new ReviewResponse(
                review.reviewId(),
                review.userId(),
                review.userName(),
                review.star(),
                review.description()
        );
    }
}
