package com.sparta.deliverit.review.presentation.dto.response;

import java.math.BigDecimal;

public record ReviewResponse(
        Long reviewId,
        String userId,
        BigDecimal star,
        String description
) {
}
