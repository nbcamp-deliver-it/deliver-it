package com.sparta.deliverit.review.presentation.controller;

import com.sparta.deliverit.review.presentation.dto.request.UpdateReviewRequest;
import com.sparta.deliverit.review.presentation.dto.response.MutateReviewResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/reviews")
public class ReviewControllerV1 {

    @PutMapping("/{reviewId}")
    public ResponseEntity<MutateReviewResponse> update(
            @PathVariable Long reviewId,
            @RequestBody @Valid UpdateReviewRequest request
    ) {
        return ResponseEntity.ok(new MutateReviewResponse(1L));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<MutateReviewResponse> delete(
            @PathVariable Long reviewId
    ) {
        return ResponseEntity.ok(new MutateReviewResponse(1L));
    }
}
