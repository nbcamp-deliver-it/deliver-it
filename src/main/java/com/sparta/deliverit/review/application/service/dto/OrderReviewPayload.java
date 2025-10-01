package com.sparta.deliverit.review.application.service.dto;

import java.math.BigDecimal;

public record OrderReviewPayload(
        String orderId,
        Long userId,
        BigDecimal star,
        String description
) {
}
