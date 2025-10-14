package com.sparta.deliverit.review.domain.entity;

import com.sparta.deliverit.anything.entity.BaseEntity;
import com.sparta.deliverit.review.domain.vo.Review;
import com.sparta.deliverit.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;

// FIXME: Order 엔티티와의 OneToOne 참조 필요
@Entity
@Table(name = "p_order_review")
@SQLDelete(sql = "UPDATE p_order_review SET deleted_at = CURRENT_TIMESTAMP WHERE order_review_id = ?")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderReview extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_review_id")
    private Long orderReviewId;

    @Embedded
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public OrderReview(Review review, User user) {
        validateReview(review);
        this.review = review;
        this.user = user;
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
