package com.sparta.deliverit.review.presentation.dto.request;

import com.sparta.deliverit.review.presentation.dto.ValidStarField;

import java.math.BigDecimal;

public record UpdateOrderReviewRequest(
        @ValidStarField
        BigDecimal star,
        String description
) {
}
