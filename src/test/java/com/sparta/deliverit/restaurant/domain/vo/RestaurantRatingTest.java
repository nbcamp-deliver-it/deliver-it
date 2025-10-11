package com.sparta.deliverit.restaurant.domain.vo;

import com.sparta.deliverit.review.domain.vo.Review;
import com.sparta.deliverit.review.domain.vo.Star;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantRatingTest {

    @Test
    @DisplayName("음식점의 별점을 생성할 수 있다")
    void createRestaurantRating() {
        RestaurantRating restaurantRating = new RestaurantRating();

        assertNotNull(restaurantRating);
    }

    @Test
    @DisplayName("음식점의 별점의 기본값은 0이다")
    void defaultZeroValue() {
        RestaurantRating restaurantRating = new RestaurantRating();

        assertEquals(BigDecimal.valueOf(0.0), restaurantRating.getStarAvg());
        assertEquals(0L, restaurantRating.getReviewsCount());
    }

    @Nested
    class Add {

        @Test
        @DisplayName("리뷰 추가시 리뷰의 개수가 1 더해진다")
        void addReviewCount() {
            RestaurantRating restaurantRating = new RestaurantRating();
            Review newReview = review(1.0);

            RestaurantRating newRestaurantRating = restaurantRating.addReview(newReview);

            assertEquals(1L, newRestaurantRating.getReviewsCount());
        }

        @Test
        @DisplayName("기본값에서 별점 4.5 추가시 평균 4.5, 개수 1이 된다")
        void addFirst() {
            RestaurantRating restaurantRating = new RestaurantRating();
            Review newReview = review(4.5);

            RestaurantRating newRestaurantRating = restaurantRating.addReview(newReview);
            assertEquals(1L, newRestaurantRating.getReviewsCount());
            assertEquals(BigDecimal.valueOf(4.5), newRestaurantRating.getStarAvg());
            assertEquals(0L, restaurantRating.getReviewsCount());
            assertEquals(BigDecimal.valueOf(0.0), restaurantRating.getStarAvg());
        }

        @Test
        @DisplayName("리뷰 4.5 추가 후 3.2 추가시 평균 별점 3.8, 개수 2가 된다")
        void addSecond() {
            RestaurantRating restaurantRating = new RestaurantRating();
            Review review1 = review(4.5);
            Review review2 = review(3.2);

            RestaurantRating rating1 = restaurantRating.addReview(review1);
            RestaurantRating rating2 = rating1.addReview(review2);

            assertEquals(2L, rating2.getReviewsCount());
            assertEquals(BigDecimal.valueOf(3.8), rating2.getStarAvg());
        }
    }

    private Review review(double value) {
        Star star = new Star(BigDecimal.valueOf(value));
        return new Review(star);
    }
}
