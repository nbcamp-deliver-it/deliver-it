package com.sparta.deliverit.review.application.service;

import com.sparta.deliverit.review.application.service.dto.OrderReviewCommand;
import com.sparta.deliverit.review.application.service.dto.OrderReviewInfo;
import com.sparta.deliverit.review.entity.OrderReview;
import com.sparta.deliverit.review.entity.Review;
import com.sparta.deliverit.review.infrastructure.repository.OrderReviewRepository;
import com.sparta.deliverit.review.infrastructure.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReviewService {
    private final OrderReviewRepository orderReviewRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public Long createReview(OrderReviewCommand command) {
        Review review = new Review(command.star(), command.description());
        Review savedReview = reviewRepository.save(review);
        orderReviewRepository.save(new OrderReview(savedReview));
        return savedReview.getReviewId();
    }

    @Transactional(readOnly = true)
    public List<OrderReviewInfo> getOrderReviews(String orderId) {
        // FIXME: 존재하는 주문인지 검증
        // FIXME: 페이지네이션 적용
        List<Review> reviews = reviewRepository.findAll(); // 임시 코드
        return OrderReviewInfo.fromList(reviews);
    }
}
