package com.sparta.deliverit.review.application.service;

import com.sparta.deliverit.global.exception.ReviewException;
import com.sparta.deliverit.global.exception.UserException;
import com.sparta.deliverit.global.response.code.ReviewResponseCode;
import com.sparta.deliverit.global.response.code.UserResponseCode;
import com.sparta.deliverit.review.application.service.dto.OrderReviewCommand;
import com.sparta.deliverit.review.application.service.dto.OrderReviewInfo;
import com.sparta.deliverit.review.domain.entity.OrderReview;
import com.sparta.deliverit.review.domain.vo.Review;
import com.sparta.deliverit.review.domain.vo.Star;
import com.sparta.deliverit.review.infrastructure.repository.OrderReviewRepository;
import com.sparta.deliverit.user.domain.entity.User;
import com.sparta.deliverit.user.domain.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Transactional
    public Long createReview(OrderReviewCommand.Create command) {
        Star star = new Star(command.star());
        Review review = new Review(star, command.description());
        User user = getUserByUserId(command.userId());
        OrderReview savedOrderReview = orderReviewRepository.save(new OrderReview(review, user));
        // FIXME: 음식점 리뷰 계산
        return savedOrderReview.getOrderReviewId();
    }

    @Transactional(readOnly = true)
    public List<OrderReviewInfo> getOrderReviews(String orderId) {
        // FIXME: 페이지네이션 적용
        List<OrderReview> orderReviews = orderReviewRepository.findAllWithUser();
        return OrderReviewInfo.fromList(orderReviews);
    }

    @Transactional
    public Long updateReview(OrderReviewCommand.Update command) {
        OrderReview orderReview = getOrderReview(command.reviewId());
        Star star = new Star(command.star());
        Review newReview = new Review(star, command.description());
        orderReview.changeReview(newReview);
        // FIXME: 음식점 리뷰 계산
        return orderReview.getOrderReviewId();
    }

    @Transactional
    public Long deleteReview(Long reviewId) {
        OrderReview orderReview = getOrderReview(reviewId);
        orderReviewRepository.delete(orderReview);
        // FIXME: 음식점 리뷰 계산
        return orderReview.getOrderReviewId();
    }

    private OrderReview getOrderReview(Long reviewId) {
        OrderReview orderReview = orderReviewRepository.findById(reviewId).orElseThrow(() -> {
            log.error("존재하지 않는 리뷰입니다. id : {}", reviewId);
            return new ReviewException(ReviewResponseCode.NOT_FOUND_ORDER_REVIEW);
        });
        return orderReview;
    }

    private User getUserByUserId(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 유저입니다. id : {}", userId);
                    return new UserException(UserResponseCode.NOT_FOUND_USER);
                });

        return user;
    }
}
