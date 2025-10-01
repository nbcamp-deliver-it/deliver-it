package com.sparta.deliverit.review.presentation.dto.request;

import com.sparta.deliverit.review.application.service.dto.OrderReviewCommand;
import com.sparta.deliverit.review.presentation.dto.ValidStarField;

import java.math.BigDecimal;

public record UpdateReviewRequest(
        @ValidStarField
        BigDecimal star,
        String description
) {
    public OrderReviewCommand.Update toCommand(Long reviewId) {
        return new OrderReviewCommand.Update(
                reviewId,
                star,
                description
        );
    }
}
