package com.sparta.deliverit.review.application.service;

import com.sparta.deliverit.global.exception.ReviewException;
import com.sparta.deliverit.global.exception.UserException;
import com.sparta.deliverit.global.response.code.OrderResponseCode;
import com.sparta.deliverit.global.response.code.UserResponseCode;
import com.sparta.deliverit.order.domain.entity.Order;
import com.sparta.deliverit.order.exception.NotFoundOrderException;
import com.sparta.deliverit.order.infrastructure.OrderRepository;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sparta.deliverit.global.response.code.ReviewResponseCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderReviewService {
    private final OrderReviewRepository orderReviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createReview(OrderReviewCommand.Create command) {
        User user = getUserByUserId(command.userId());
        Order order = getOrderById(command.orderId());

        Star star = new Star(command.star());
        Review review = new Review(star, command.description());
        try {
            OrderReview savedOrderReview = orderReviewRepository.save(new OrderReview(review, user, order));
            Restaurant restaurant = order.getRestaurant();
            restaurant.addReview(review);
            return savedOrderReview.getOrderReviewId();
        } catch (DataIntegrityViolationException e) {
            log.error("이미 해당 주문에 리뷰가 존재합니다.");
            throw new ReviewException(DUPLICATE_ORDER_REVIEW);
        }
    }

    @Transactional(readOnly = true)
    public List<OrderReviewInfo> getOrderReviews(String orderId) {
        // FIXME: 페이지네이션 적용
        Order order = getOrderById(orderId);
        List<OrderReview> orderReviews = orderReviewRepository.findAllByOrder(order);
        return OrderReviewInfo.fromList(orderReviews);
    }

    @Transactional
    public Long updateReview(OrderReviewCommand.Update command) {
        OrderReview orderReview = getOrderReview(command.reviewId());
        Review oldReview = orderReview.getReview();

        Star star = new Star(command.star());
        Review newReview = new Review(star, command.description());
        orderReview.changeReview(newReview);

        Order order = orderReview.getOrder();
        Restaurant restaurant = order.getRestaurant();
        restaurant.updateReview(oldReview, newReview);
        return orderReview.getOrderReviewId();
    }

    @Transactional
    public Long deleteReview(Long reviewId) {
        OrderReview orderReview = getOrderReview(reviewId);
        Order order = orderReview.getOrder();
        Restaurant restaurant = order.getRestaurant();

        orderReview.deactivate();
        restaurant.removeReview(orderReview.getReview());
        return orderReview.getOrderReviewId();
    }

    private OrderReview getOrderReview(Long reviewId) {
        OrderReview orderReview = orderReviewRepository.findById(reviewId).orElseThrow(() -> {
            log.error("존재하지 않는 리뷰입니다. id : {}", reviewId);
            return new ReviewException(NOT_FOUND_ORDER_REVIEW);
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

    private Order getOrderById(String orderId) {
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 주문입니다. orderId: {}", orderId);
                    return new NotFoundOrderException(OrderResponseCode.NOT_FOUND_ORDER);
                });
        return order;
    }
}
