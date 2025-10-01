package com.sparta.deliverit.review.application.service;

import com.sparta.deliverit.review.application.service.dto.OrderReviewPayload;
import com.sparta.deliverit.review.entity.OrderReview;
import com.sparta.deliverit.review.entity.Review;
import com.sparta.deliverit.review.infrastructure.repository.OrderReviewRepository;
import com.sparta.deliverit.review.infrastructure.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderReviewService {
    private final OrderReviewRepository orderReviewRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public Long createReview(OrderReviewPayload payload) {
        Review review = new Review(payload.star(), payload.description());
        Review savedReview = reviewRepository.save(review);
        orderReviewRepository.save(new OrderReview(savedReview));
        return savedReview.getReviewId();
    }
}
