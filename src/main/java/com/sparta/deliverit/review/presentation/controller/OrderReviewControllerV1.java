package com.sparta.deliverit.review.presentation.controller;

import com.sparta.deliverit.review.presentation.dto.request.CreateOrderReviewRequest;
import com.sparta.deliverit.review.presentation.dto.request.UpdateOrderReviewRequest;
import com.sparta.deliverit.review.presentation.dto.response.MutateOrderReviewResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/orders")
public class OrderReviewControllerV1 {

    @PostMapping("/{orderId}/reviews")
    public ResponseEntity<MutateOrderReviewResponse> create(
            @PathVariable
            String orderId,
            @Valid
            @RequestBody
            CreateOrderReviewRequest request
    ) {
        return ResponseEntity.ok(new MutateOrderReviewResponse(1L));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<MutateOrderReviewResponse> delete(
            @PathVariable
            @Positive(message = "해당 reviewId 는 존재하지 않습니다.")
            long reviewId
    ) {
        return ResponseEntity.ok(new MutateOrderReviewResponse(1L));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<MutateOrderReviewResponse> update(
        @PathVariable
        @Positive(message = "해당 reviewId 는 존재하지 않습니다.")
        long reviewId,
        @RequestBody
        @Valid
        UpdateOrderReviewRequest request
    ) {
        return ResponseEntity.ok(new MutateOrderReviewResponse(1L));
    }
}
