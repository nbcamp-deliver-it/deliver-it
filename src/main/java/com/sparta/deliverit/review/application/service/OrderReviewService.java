package com.sparta.deliverit.review.application.service;

import com.sparta.deliverit.review.application.service.dto.OrderReviewCommand;
import com.sparta.deliverit.review.application.service.dto.OrderReviewInfo;
import com.sparta.deliverit.review.entity.OrderReview;
import com.sparta.deliverit.review.entity.Review;
import com.sparta.deliverit.review.infrastructure.repository.OrderReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderReviewService {
    private final OrderReviewRepository orderReviewRepository;

    public Long createReview(OrderReviewCommand.Create command) {
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

    @Transactional
    public Long updateReview(OrderReviewCommand.Update command) {
        OrderReview orderReview = orderReviewRepository.findById(command.reviewId()).orElseThrow(() -> {
            log.error("존재하지 않는 리뷰입니다. id : {}", command.reviewId());
            // FIXME: 도메인 예외로 변경 필요
            throw new IllegalArgumentException("");
        });
        Review newReview = new Review(command.star(), command.description());
        orderReview.changeReview(newReview);
        return orderReview.getOrderReviewId();
    }
}
