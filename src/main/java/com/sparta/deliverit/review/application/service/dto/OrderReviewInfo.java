package com.sparta.deliverit.review.application.service.dto;

import com.sparta.deliverit.review.entity.Review;

import java.math.BigDecimal;
import java.util.List;

public record OrderReviewInfo(
        Long reviewId,
        Long userId,
        String userName,
        BigDecimal star,
        String description
) {
    public static List<OrderReviewInfo> fromList(List<Review> reviews) {
        return reviews.stream().map(OrderReviewInfo::from).toList();
    }

    public static OrderReviewInfo from(Review review) {
        return new OrderReviewInfo(
                review.getReviewId(),
                1L,
                "userName",
                review.getStar(),
                review.getDescription()
        );
    }
}
