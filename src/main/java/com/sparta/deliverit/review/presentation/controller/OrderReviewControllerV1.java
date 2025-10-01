package com.sparta.deliverit.review.presentation.controller;

import com.sparta.deliverit.review.application.service.OrderReviewService;
import com.sparta.deliverit.review.presentation.dto.request.CreateOrderReviewRequest;
import com.sparta.deliverit.review.presentation.dto.request.UpdateReviewRequest;
import com.sparta.deliverit.review.presentation.dto.response.MutateReviewResponse;
import com.sparta.deliverit.review.presentation.dto.response.OrderReviewListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderReviewControllerV1 {
    private final OrderReviewService orderReviewService;

    @GetMapping("orders/{orderId}/reviews")
    public ResponseEntity<OrderReviewListResponse> getOrderReviews(
            @PathVariable String orderId
    ) {
        var orderReviews = orderReviewService.getOrderReviews(orderId);
        return ResponseEntity.ok(OrderReviewListResponse.from(orderReviews));
    }

    @PostMapping("orders/{orderId}/reviews")
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

    @PutMapping("order-reviews/{reviewId}")
    public ResponseEntity<MutateReviewResponse> update(
            @PathVariable Long reviewId,
            @RequestBody @Valid UpdateReviewRequest request
    ) {
        return ResponseEntity.ok(new MutateReviewResponse(1L));
    }

    @DeleteMapping("order-reviews/{reviewId}")
    public ResponseEntity<MutateReviewResponse> delete(
            @PathVariable Long reviewId
    ) {
        return ResponseEntity.ok(new MutateReviewResponse(1L));
    }
}
