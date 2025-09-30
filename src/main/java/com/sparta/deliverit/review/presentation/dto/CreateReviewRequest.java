package com.sparta.deliverit.review.presentation.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateReviewRequest(
        @NotBlank(message = "orderId 는 필수값입니다.")
        String orderId,
        @NotNull(message = "userId 는 필수값입니다.")
        @Positive(message = "해당 userId 는 존재하지 않습니다.")
        Long userId,
        @NotNull(message = "star 는 필수값입니다.")
        @DecimalMin(value = "1.0", message = "별점은 최소 1.0 이상이어야 합니다.")
        @DecimalMax(value = "5.0", message = "별점은 최대 5.0 이하이어야 합니다.")
        BigDecimal star,
        String description
) {
}
