package com.sparta.deliverit.review.application.service;

import com.sparta.deliverit.review.application.service.dto.OrderReviewCommand;
import com.sparta.deliverit.review.application.service.dto.OrderReviewInfo;
import com.sparta.deliverit.review.entity.OrderReview;
import com.sparta.deliverit.review.entity.Review;
import com.sparta.deliverit.review.infrastructure.repository.OrderReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReviewService {
    private final OrderReviewRepository orderReviewRepository;

    @Transactional
    public Long createReview(OrderReviewCommand command) {
        Review review = new Review(command.star(), command.description());
        OrderReview savedOrderReview = orderReviewRepository.save(new OrderReview(review));
        return savedOrderReview.getOrderReviewId();
    }

    @Transactional(readOnly = true)
    public List<OrderReviewInfo> getOrderReviews(String orderId) {
        // FIXME: 페이지네이션 적용
        List<OrderReview> orderReviews = orderReviewRepository.findAll();
        return OrderReviewInfo.fromList(orderReviews);
    }
}
