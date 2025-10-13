package com.sparta.deliverit.review.domain.entity;

import com.sparta.deliverit.review.domain.vo.Review;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// FIXME: Order 엔티티와의 OneToOne 참조 필요
// FIXME: User 엔티티와의 ManyToOne 참조 필요
@Entity
@Table(name = "p_order_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_review_id")
    private Long orderReviewId;

    @Embedded
    private Review review;

    public OrderReview(Review review) {
        validateReview(review);
        this.review = review;
    }

    public void changeReview(Review newReview) {
        validateReview(newReview);
        this.review = newReview;
    }

    public BigDecimal getStar() {
        return review.getStar();
    }

    public String getDescription() {
        return review.getDescription();
    }

    private void validateReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("리뷰는 필수입니다.");
        }
    }
}
