package com.sparta.deliverit.review.application.service.dto;

import com.sparta.deliverit.review.entity.OrderReview;

import java.math.BigDecimal;
import java.util.List;

public record OrderReviewInfo(
        Long orderReviewId,
        Long userId,
        String userName,
        BigDecimal star,
        String description
) {
    public static List<OrderReviewInfo> fromList(List<OrderReview> reviews) {
        return reviews.stream().map(OrderReviewInfo::from).toList();
    }

    public static OrderReviewInfo from(OrderReview review) {
        return new OrderReviewInfo(
                review.getOrderReviewId(),
                1L,
                "userName",
                review.getStar(),
                review.getDescription()
        );
    }
}
