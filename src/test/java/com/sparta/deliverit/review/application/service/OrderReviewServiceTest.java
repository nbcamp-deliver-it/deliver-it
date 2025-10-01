package com.sparta.deliverit.review.application.service;

import com.sparta.deliverit.review.application.service.dto.OrderReviewCommand;
import com.sparta.deliverit.review.entity.OrderReview;
import com.sparta.deliverit.review.entity.Review;
import com.sparta.deliverit.review.infrastructure.repository.OrderReviewRepository;
import com.sparta.deliverit.review.infrastructure.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderReviewServiceTest {

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    OrderReviewRepository orderReviewRepository;

    @InjectMocks
    OrderReviewService orderReviewService;

    @Test
    @DisplayName("주문 리뷰 생성시 주문 리뷰와 리뷰 모두 생성한다")
    void createOrderReview() {
        var payload = new OrderReviewCommand(
                "orderId",
                1L,
                BigDecimal.valueOf(4.5),
                "리뷰 설명"
        );

        when(reviewRepository.save(any(Review.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);
        when(orderReviewRepository.save(any(OrderReview.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);
        orderReviewService.createReview(payload);

        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(orderReviewRepository, times(1)).save(any(OrderReview.class));
    }
}
