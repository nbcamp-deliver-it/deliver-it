package com.sparta.deliverit.review.infrastructure.repository;

import com.sparta.deliverit.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
