package com.sparta.deliverit.review.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    @DisplayName("리뷰를 생성할 수 있다")
    void createReview() {
        var review = new Review(BigDecimal.valueOf(4.5), "리뷰 내용");

        assertNotNull(review);
        assertEquals(BigDecimal.valueOf(4.5), review.getStar());
    }

    @Test
    @DisplayName("별점이 Null 이라면 예외가 발생한다")
    void failWhenStarIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Review(null));
    }

    @Test
    @DisplayName("리뷰 내용이 없더라도 생성할 수 있다")
    void createReviewWithoutDescription() {
        Review review = new Review(BigDecimal.valueOf(4.5));

        assertNotNull(review);
        assertNull(review.getDescription());
    }

    @Test
    @DisplayName("리뷰 별점은 소수점 1자리까지만 보장한다")
    void starIsTruncatedToOneDecimalPlace() {
        Review review = new Review(BigDecimal.valueOf(4.5555555555));

        assertEquals(BigDecimal.valueOf(4.5), review.getStar());
    }

    @Test
    @DisplayName("리뷰 별점은 1.0 이상이어야 한다")
    void failWhenStarIsLessThanOne() {
        assertThrows(IllegalArgumentException.class, () ->
                new Review(BigDecimal.valueOf(0.9)));
    }

    @Test
    @DisplayName("리뷰 별점은 5.0 이하이어야 한다")
    void failWhenStarIsGreaterThanFive() {
        assertThrows(IllegalArgumentException.class, () ->
                new Review(BigDecimal.valueOf(5.1)));
    }

    @Test
    @DisplayName("리뷰 별점 변경시 1.0 미만 혹은 5.0 초과인 경우 별점을 변경할 수 없다")
    void failWhenChangingStarOutOfRange() {
        Review review = new Review(BigDecimal.valueOf(4.5));

        assertThrows(IllegalArgumentException.class, () ->
                review.changeStar(BigDecimal.valueOf(0.9)));
        assertThrows(IllegalArgumentException.class, () ->
                review.changeStar(BigDecimal.valueOf(5.1)));
    }
}
