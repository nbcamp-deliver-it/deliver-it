package com.sparta.deliverit.order.infrastructure;

import com.sparta.deliverit.order.domain.entity.Order;
import com.sparta.deliverit.order.domain.entity.OrderStatus;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForOwner;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
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
            o.totalPrice AS totalPrice
        FROM Order o
        JOIN o.user u 
        JOIN o.restaurant r
        WHERE o.user.id = :userId
            and o.orderedAt >= :from
            and o.orderedAt < :to    
        ORDER BY o.orderedAt DESC, o.orderId desc
    """)
    Page<OrderDetailForUser> findOrdersByUserIdForUser(
            @Param("userId") String userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

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
        WHERE r.restaurantId = :restaurantId
            and o.orderedAt >= :from
            and o.orderedAt < :to    
        ORDER BY o.orderedAt DESC, o.orderId desc
    """)
    Page<OrderDetailForOwner> findOrdersByRestaurantIdForOwner(
            @Param("restaurantId") String restaurantId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
                UPDATE Order o
                SET o.orderStatus = :nextStatus,
                    o.version = o.version + 1
                WHERE o.orderId = :orderId
                    AND o.orderStatus = :currStatus
                    AND o.restaurant.restaurantId = :restaurantId
                    AND o.restaurant.user.id = :accessUserId
                    AND o.version = :version
                    AND o.orderedAt > :nowMinusMinute
            """)
    int updateOrderStatusToConfirm(@Param("orderId") String orderId, @Param("restaurantId") String restaurantId, @Param("accessUserId") Long accessUserId, @Param("currStatus") OrderStatus currStatus, @Param("nextStatus") OrderStatus nextStatus, @Param("version") Long version, @Param("nowMinusMinute") LocalDateTime nowMinusMinute);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
    """
        UPDATE Order o
        SET o.orderStatus = :nextStatus,
            o.version = o.version + 1
        WHERE o.orderId = :orderId
            AND o.user.id = :accessUserId
            AND o.orderStatus = :currStatus
            AND o.version = :version
            AND o.orderedAt > :nowMinusMinute
    """)
    int updateOrderStatusToCancelForUser(@Param("orderId") String orderId, @Param("accessUserId") Long accessUserId, @Param("currStatus") OrderStatus currStatus, @Param("nextStatus") OrderStatus nextStatus, @Param("version") Long version, @Param("nowMinusMinute") LocalDateTime nowMinusMinute);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
    """
        UPDATE Order o
        SET o.orderStatus = :nextStatus,
            o.version = o.version + 1
        WHERE o.orderId = :orderId
            AND o.restaurant.restaurantId = :restaurantId
            AND o.restaurant.user.id = :accessUserId
            AND o.orderStatus IN :currStatusList
            AND o.orderStatus <> :nextStatus
            AND o.version = :version
    """)
    int updateOrderStatusToCancelForOwner(@Param("orderId") String orderId, @Param("restaurantId") String restaurantId, @Param("accessUserId") Long accessUserId, @Param("currStatusList") List<OrderStatus> currStatusList, @Param("nextStatus") OrderStatus nextStatus, @Param("version") Long version);
}
