package com.sparta.deliverit.review.presentation.controller;

import com.sparta.deliverit.review.presentation.dto.CreateReviewRequest;
import com.sparta.deliverit.review.presentation.dto.CreateReviewResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/reviews")
public class ReviewControllerV1 {

    @PostMapping
    public ResponseEntity<CreateReviewResponse> create(
            @RequestBody CreateReviewRequest request
    ) {
        return ResponseEntity.ok(new CreateReviewResponse(1L));
    }
}
