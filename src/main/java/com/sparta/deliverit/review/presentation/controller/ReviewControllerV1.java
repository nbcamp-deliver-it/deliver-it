package com.sparta.deliverit.review.presentation.controller;

import com.sparta.deliverit.review.presentation.dto.CreateReviewRequest;
import com.sparta.deliverit.review.presentation.dto.MutateReviewResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/reviews")
public class ReviewControllerV1 {

    @PostMapping
    public ResponseEntity<MutateReviewResponse> create(
            @Valid @RequestBody CreateReviewRequest request
    ) {
        return ResponseEntity.ok(new MutateReviewResponse(1L));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<MutateReviewResponse> delete(
            @PathVariable
            @Positive(message = "해당 reviewId 는 존재하지 않습니다.")
            long reviewId
    ) {
        return ResponseEntity.ok(new MutateReviewResponse(1L));
    }
}
