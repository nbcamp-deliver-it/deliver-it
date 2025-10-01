package com.sparta.deliverit.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// FIXME: Order 엔티티와의 ManyToOne 참조 필요
@Entity
@Table(name = "p_order_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_review_id")
    private Long orderReviewId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    public OrderReview(Review review) {
        validateReview(review);
        this.review = review;
    }

    private void validateReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("리뷰는 필수입니다.");
        }
    }
}
