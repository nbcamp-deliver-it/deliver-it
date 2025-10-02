package com.sparta.deliverit.review.presentation.controller;

import com.sparta.deliverit.review.application.service.OrderReviewService;
import com.sparta.deliverit.review.presentation.dto.request.CreateOrderReviewRequest;
import com.sparta.deliverit.review.presentation.dto.request.UpdateReviewRequest;
import com.sparta.deliverit.review.presentation.dto.response.MutateReviewResponse;
import com.sparta.deliverit.review.presentation.dto.response.OrderReviewListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderReviewControllerV1 {
    private final OrderReviewService orderReviewService;

    @GetMapping("orders/{orderId}/reviews")
    public ResponseEntity<OrderReviewListResponse> getOrderReviews(
            @PathVariable String orderId
    ) {
        log.info("=== 주문 리뷰 조회 orderId : {} ===", orderId);
        var orderReviews = orderReviewService.getOrderReviews(orderId);
        log.info("=== 주문 리뷰 조회 성공 ===");
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
        log.info("=== 주문 리뷰 생성 orderId : {} ===", orderId);
        var command = request.toCommand(orderId);
        Long savedReviewId = orderReviewService.createReview(command);
        log.info("=== 주문 리뷰 생성 성공 ===");
        return ResponseEntity.ok(new MutateReviewResponse(savedReviewId));
    }

    @PutMapping("order-reviews/{orderReviewId}")
    public ResponseEntity<MutateReviewResponse> update(
            @PathVariable Long orderReviewId,
            @RequestBody @Valid UpdateReviewRequest request
    ) {
        log.info("=== 주문 리뷰 수정 order-reviewId : {} ===", orderReviewId);
        var command = request.toCommand(orderReviewId);
        Long id = orderReviewService.updateReview(command);
        log.info("=== 주문 리뷰 수정 성공 ===");
        return ResponseEntity.ok(new MutateReviewResponse(id));
    }

    @DeleteMapping("order-reviews/{orderReviewId}")
    public ResponseEntity<MutateReviewResponse> delete(
            @PathVariable Long orderReviewId
    ) {
        log.info("=== 주문 리뷰 삭제 order-reviewId : {} ===", orderReviewId);
        Long id = orderReviewService.deleteReview(orderReviewId);
        log.info("=== 주문 리뷰 삭제 성공 ===");
        return ResponseEntity.ok(new MutateReviewResponse(id));
    }
}
