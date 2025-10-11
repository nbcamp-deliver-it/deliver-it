package com.sparta.deliverit.review.domain.vo;

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
    @DisplayName("리뷰의 별점과 설명이 같으면 같은 리뷰이다")
    void whenStarAndDescriptionAreSameThenEquals() {
        var review1 = new Review(BigDecimal.valueOf(4.5), "리뷰 생성");
        var review2 = new Review(BigDecimal.valueOf(4.5), "리뷰 생성");

        assertEquals(review1, review2);
    }

    @Test
    @DisplayName("리뷰의 별점과 설명이 다르면 다른 리뷰이다")
    void whenStarAndDescriptionAreDifferentThenNotEquals() {
        var review1 = new Review(BigDecimal.valueOf(4.5), "리뷰 생성");
        var review2 = new Review(BigDecimal.valueOf(1.0), "리뷰 생성");
        var review3 = new Review(BigDecimal.valueOf(4.5), "리뷰");

        assertNotEquals(review1, review2);
        assertNotEquals(review1, review3);
    }
}
