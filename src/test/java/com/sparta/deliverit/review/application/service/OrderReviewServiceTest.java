package com.sparta.deliverit.review.application.service;

import com.sparta.deliverit.order.domain.entity.Order;
import com.sparta.deliverit.order.infrastructure.OrderRepository;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import com.sparta.deliverit.review.application.service.dto.OrderReviewCommand;
import com.sparta.deliverit.review.domain.entity.OrderReview;
import com.sparta.deliverit.review.infrastructure.repository.OrderReviewRepository;
import com.sparta.deliverit.user.domain.entity.User;
import com.sparta.deliverit.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderReviewServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderReviewRepository orderReviewRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    OrderReviewService orderReviewService;

    @Test
    @DisplayName("주문 리뷰를 생성할 수 있다")
    void createOrderReview() {
        var payload = new OrderReviewCommand.Create(
                "orderId",
                1L,
                BigDecimal.valueOf(4.5),
                "리뷰 설명"
        );

        when(orderReviewRepository.save(any(OrderReview.class)))
                .thenAnswer(invocation -> invocation.getArguments()[0]);
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(new User()));
        when(orderRepository.findById(any(String.class)))
                .thenReturn(Optional.of(Order.builder().restaurant(new Restaurant()).build()));
        orderReviewService.createReview(payload);

        verify(orderReviewRepository, times(1)).save(any(OrderReview.class));
    }
}
