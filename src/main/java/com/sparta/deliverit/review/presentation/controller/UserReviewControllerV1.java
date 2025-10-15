package com.sparta.deliverit.review.presentation.controller;

import com.sparta.deliverit.global.presentation.dto.Result;
import com.sparta.deliverit.global.response.code.ReviewResponseCode;
import com.sparta.deliverit.review.application.service.UserReviewService;
import com.sparta.deliverit.review.application.service.dto.OrderReviewInfo;
import com.sparta.deliverit.review.presentation.dto.response.UserReviewListResponse;
import com.sparta.deliverit.review.presentation.dto.response.UserReviewResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

import static com.sparta.deliverit.global.response.code.ReviewResponseCode.*;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserReviewControllerV1 {
    private final UserReviewService userReviewService;

    @GetMapping("/{userId}/reviews")
    public Result<UserReviewListResponse> getUserReviews(
            @PathVariable Long userId
    ) {
        log.info("=== 유저의 주문 리뷰 조회 userId : {} ===", userId);
        List<OrderReviewInfo> userReviews = userReviewService.getUserReviews(userId);
        log.info("=== 유저의 주문 리뷰 조회 성공 ===");
        return Result.of(
                USER_REVIEW_QUERY_SUCCESS.getMessage(),
                USER_REVIEW_QUERY_SUCCESS.name(),
                UserReviewListResponse.from(userReviews)
        );
    }
}
