package com.sparta.deliverit.order.infrastructure;

import com.sparta.deliverit.order.domain.entity.Order;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForOwner;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Query(
    """
        SELECT 
            u.id AS userId,
            u.username AS userName,    
            r.restaurantId AS restaurantId,
            r.name AS restaurantName,
            o.orderId AS orderId,
            o.orderedAt AS orderedAt,
            o.orderStatus AS orderStatus,
            o.address AS address,
            o.totalPrice AS totalPrice,
            o.version AS version
        FROM Order o
        JOIN o.user u 
        JOIN o.restaurant r
        WHERE o.orderId =:orderId
    """)
    Optional<OrderDetailForUser> getByOrderIdForUser(@Param("orderId") String orderId);

    @Query(
    """
       SELECT 
            u.id AS userId,
            u.username AS userName,    
            r.restaurantId AS restaurantId,
            r.name AS restaurantName,
            r.user.id AS restaurantUserId,
            o.orderId AS orderId,
            o.orderedAt AS orderedAt,
            o.orderStatus AS orderStatus,
            o.address AS address,
            o.totalPrice AS totalPrice,
            o.version AS version
        FROM Order o
        JOIN o.user u 
        JOIN o.restaurant r
        WHERE o.orderId =:orderId 
    """)
    Optional<OrderDetailForOwner> getByOrderIdForOwner(@Param("orderId") String orderId);
}
