package com.sparta.deliverit.review.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.math.RoundingMode;

// FIXME: User 엔티티와의 ManyToOne 참조 필요
@Entity
@Table(name = "p_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "star", precision = 2, scale = 1, nullable = false)
    @Comment("별점")
    private BigDecimal star;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT", nullable = true)
    @Comment("리뷰 내용")
    private String description;

    private final BigDecimal STAR_MIN = BigDecimal.valueOf(1.0);
    private final BigDecimal STAR_MAX = BigDecimal.valueOf(5.0);

    public Review(BigDecimal star) {
        this.star = validateStar(star);
    }

    public Review(BigDecimal star, String description) {
        this.star = validateStar(star);
        this.description = description;
    }

    public void changeStar(BigDecimal newStar) {
        this.star = validateStar(newStar);
    }

    private BigDecimal validateStar(BigDecimal star) {
        if (star == null) {
            throw new IllegalArgumentException("별점은 필수입니다.");
        }
        BigDecimal value = normalizeStar(star);

        if (value.compareTo(STAR_MIN) < 0 || value.compareTo(STAR_MAX) > 0) {
            throw new IllegalArgumentException("별점은 1.0 이상 5.0 이하이어야 합니다.");
        }
        return value;
    }

    private BigDecimal normalizeStar(BigDecimal star) {
        return star.setScale(1, RoundingMode.DOWN);
    }
}
