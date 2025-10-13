package com.sparta.deliverit.review.presentation.dto.response;

import com.sparta.deliverit.review.application.service.dto.OrderReviewInfo;

import java.math.BigDecimal;

public record UserReviewResponse(
        BigDecimal star,
        String description
) {
    public static UserReviewResponse from(OrderReviewInfo review) {
        return new UserReviewResponse(
                review.star(),
                review.description()
        );
    }
}
