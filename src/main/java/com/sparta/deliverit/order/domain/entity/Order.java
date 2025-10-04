package com.sparta.deliverit.order.domain.entity;

import com.sparta.deliverit.anything.entity.BaseEntity;
import com.sparta.deliverit.payment.domain.entity.Payment;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import com.sparta.deliverit.user.domain.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "p_order")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id")
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_order_user"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_order_restaurant"))
    private Restaurant restaurant;

    @OneToOne(fetch = FetchType.LAZY)
    private Payment payment;

    @Column(name = "ordered_at", nullable = false, updatable = false)
    LocalDateTime orderedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private String address;

    @Column(name = "total_price", nullable = false, updatable = false)
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItemList = new ArrayList<>();

    protected Order() {

    }
}
