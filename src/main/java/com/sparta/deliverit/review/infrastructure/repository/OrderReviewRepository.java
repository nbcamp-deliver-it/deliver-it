package com.sparta.deliverit.review.infrastructure.repository;

import com.sparta.deliverit.review.domain.entity.OrderReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderReviewRepository extends JpaRepository<OrderReview, Long> {
    @Query("""
        SELECT r 
        FROM OrderReview r
        JOIN FETCH r.user u
        order by r.createdAt DESC
    """)
    List<OrderReview> findAllWithUser();

    @Query("""
        SELECT r 
        FROM OrderReview r
        JOIN FETCH r.user u
        WHERE u.id = :userId
        order by r.createdAt DESC
    """)
    List<OrderReview> findAllByUserIdWithUser(
            @Param("userId") Long userId
    );
}
