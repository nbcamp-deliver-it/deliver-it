package com.sparta.deliverit.review.presentation.dto;

import java.math.BigDecimal;

public record CreateReviewRequest(
       String orderId,
       Long userId,
       BigDecimal star,
       String description
) {
}
