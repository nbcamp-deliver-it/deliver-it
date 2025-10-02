package com.sparta.deliverit.review.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderReviewTest {

    @Test
    @DisplayName("주문 리뷰를 생성할 수 있다")
    void createOrderReview() {
        var review = new Review(BigDecimal.valueOf(4.5), "리뷰 내용");
        OrderReview orderReview = new OrderReview(review);

        assertNotNull(orderReview);
    }

    @Test
    @DisplayName("리뷰가 Null 이라면 예외가 발생한다")
    void failWhenReviewIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new OrderReview(null));
    }

    @Test
    @DisplayName("주문의 리뷰는 변경할 수 있다")
    void changeReview() {
        var oldReview = new Review(BigDecimal.valueOf(4.5), "예전 리뷰");
        OrderReview orderReview = new OrderReview(oldReview);

        var newReview = new Review(BigDecimal.valueOf(1.0), "새로운 리뷰");
        orderReview.changeReview(newReview);

        assertEquals(newReview, orderReview.getReview());
        assertEquals(newReview.getStar(), orderReview.getStar());
        assertEquals(newReview.getDescription(), orderReview.getDescription());
    }
}
