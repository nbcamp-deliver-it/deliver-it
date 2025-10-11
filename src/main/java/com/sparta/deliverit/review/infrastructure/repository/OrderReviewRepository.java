package com.sparta.deliverit.review.infrastructure.repository;

import com.sparta.deliverit.review.domain.entity.OrderReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderReviewRepository extends JpaRepository<OrderReview, Long> {
}
