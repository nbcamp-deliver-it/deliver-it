package com.sparta.deliverit.review.infrastructure.repository;

import com.sparta.deliverit.review.entity.OrderReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderReviewRepository extends JpaRepository<OrderReview, Long> {

    // FIXME: createdAt 과 Order 연관관계 생긴 이후 조회 필요
    /*
    @Query("""
        SELECT orv
        FROM OrderReview orv
        JOIN orv.order o
        JOIN FETCH orv.review r
        WHERE o.orderId = :orderId
        order by orv.createdAt DESC
    """)
    List<OrderReview> findAllByOrderIdWithReviews(@Param("orderId") String orderId);
    */
}
