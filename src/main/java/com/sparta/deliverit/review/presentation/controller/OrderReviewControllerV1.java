package com.sparta.deliverit.review.presentation.controller;

import com.sparta.deliverit.review.application.service.OrderReviewService;
import com.sparta.deliverit.review.presentation.dto.request.CreateOrderReviewRequest;
import com.sparta.deliverit.review.presentation.dto.response.MutateReviewResponse;
import com.sparta.deliverit.review.presentation.dto.response.ReviewListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderReviewControllerV1 {
    private final OrderReviewService orderReviewService;

    @GetMapping("/{orderId}/reviews")
    public ResponseEntity<ReviewListResponse> getOrderReviews(
            @PathVariable String orderId
    ) {
        var reviews = orderReviewService.getOrderReviews(orderId);
        return ResponseEntity.ok(ReviewListResponse.from(reviews));
    }

    @PostMapping("/{orderId}/reviews")
    public ResponseEntity<MutateReviewResponse> create(
            @PathVariable
            String orderId,
            @Valid
            @RequestBody
            CreateOrderReviewRequest request
    ) {
        var command = request.toCommand(orderId);
        Long savedReviewId = orderReviewService.createReview(command);
        return ResponseEntity.ok(new MutateReviewResponse(savedReviewId));
    }
}
