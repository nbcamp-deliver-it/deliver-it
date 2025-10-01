package com.sparta.deliverit.review.infrastructure.repository;

import com.sparta.deliverit.review.entity.OrderReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderReviewRepository extends JpaRepository<OrderReview, Long> {
}
