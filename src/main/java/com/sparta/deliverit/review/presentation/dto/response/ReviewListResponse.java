package com.sparta.deliverit.review.presentation.dto.response;

import com.sparta.deliverit.review.application.service.dto.OrderReviewInfo;

import java.util.List;

public record ReviewListResponse(
        List<ReviewResponse> list
) {
    public static ReviewListResponse from(List<OrderReviewInfo> reviews) {
        List<ReviewResponse> list = reviews.stream().map(ReviewResponse::from).toList();
        return new ReviewListResponse(list);
    }
}
