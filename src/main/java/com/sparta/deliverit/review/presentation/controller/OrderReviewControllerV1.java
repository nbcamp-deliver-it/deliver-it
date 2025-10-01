package com.sparta.deliverit.review.presentation.controller;

import com.sparta.deliverit.review.application.service.OrderReviewService;
import com.sparta.deliverit.review.presentation.dto.request.CreateOrderReviewRequest;
import com.sparta.deliverit.review.presentation.dto.response.MutateReviewResponse;
import com.sparta.deliverit.review.presentation.dto.response.ReviewListResponse;
import com.sparta.deliverit.review.presentation.dto.response.ReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderReviewControllerV1 {
    private final OrderReviewService orderReviewService;

    @GetMapping("/{orderId}/reviews")
    public ResponseEntity<ReviewListResponse> getOrderReviews(
            @PathVariable String orderId
    ) {
        return ResponseEntity.ok(new ReviewListResponse(
                List.of(
                        new ReviewResponse(
                                1L,
                                "userId",
                                BigDecimal.valueOf(4.5),
                                "리뷰 설명"
                        )
                )
        ));
    }

    @PostMapping("/{orderId}/reviews")
    public ResponseEntity<MutateReviewResponse> create(
            @PathVariable
            String orderId,
            @Valid
            @RequestBody
            CreateOrderReviewRequest request
    ) {
        var payload = request.toPayload(orderId);
        Long savedReviewId = orderReviewService.createReview(payload);
        return ResponseEntity.ok(new MutateReviewResponse(savedReviewId));
    }
}
