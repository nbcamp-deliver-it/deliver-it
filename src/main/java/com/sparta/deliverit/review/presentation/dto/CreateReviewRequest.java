package com.sparta.deliverit.review.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateReviewRequest(
        @NotBlank(message = "orderId 는 필수값입니다.")
        String orderId,
        @NotNull(message = "userId 는 필수값입니다.")
        Long userId,
        @NotNull(message = "star 는 필수값입니다.")
        BigDecimal star,
        String description
) {
}
