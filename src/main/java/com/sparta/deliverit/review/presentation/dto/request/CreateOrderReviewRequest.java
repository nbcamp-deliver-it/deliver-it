package com.sparta.deliverit.review.presentation.dto.request;

import com.sparta.deliverit.review.application.service.dto.OrderReviewPayload;
import com.sparta.deliverit.review.presentation.dto.ValidStarField;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateOrderReviewRequest(
        @NotNull(message = "userId 는 필수값입니다.")
        @Positive(message = "해당 userId 는 존재하지 않습니다.")
        Long userId,
        @ValidStarField
        BigDecimal star,
        String description
) {
    public OrderReviewPayload toPayload(String orderId) {
        return new OrderReviewPayload(
                orderId,
                userId,
                star,
                description
        );
    }
}
