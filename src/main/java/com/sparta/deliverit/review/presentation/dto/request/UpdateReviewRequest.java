package com.sparta.deliverit.review.presentation.dto.request;

import com.sparta.deliverit.review.application.service.dto.OrderReviewCommand;
import com.sparta.deliverit.review.presentation.dto.ValidStarField;

import java.math.BigDecimal;

public record UpdateReviewRequest(
        @ValidStarField
        BigDecimal star,
        String description
) {
    public OrderReviewCommand.Update toCommand(Long orderReviewId) {
        return new OrderReviewCommand.Update(
                orderReviewId,
                star,
                description
        );
    }
}
