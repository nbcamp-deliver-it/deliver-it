package com.sparta.deliverit.review.presentation.dto.response;

import java.math.BigDecimal;

public record UserReviewResponse(
        BigDecimal star,
        String description
) {
}
