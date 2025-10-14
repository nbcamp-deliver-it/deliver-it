package com.sparta.deliverit.review.presentation.dto.response;

import com.sparta.deliverit.review.application.service.dto.OrderReviewInfo;

import java.util.List;

public record UserReviewListResponse(
        List<UserReviewResponse> list
) {
    public static UserReviewListResponse from(List<OrderReviewInfo> reviews) {
        List<UserReviewResponse> list = reviews.stream().map(UserReviewResponse::from).toList();
        return new UserReviewListResponse(list);
    }
}
