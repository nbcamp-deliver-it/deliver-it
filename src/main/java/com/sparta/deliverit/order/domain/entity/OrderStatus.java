package com.sparta.deliverit.order.domain.entity;

public enum OrderStatus {
    ORDER_CREATED("주문 생성"),
    PAYMENT_PENDING("결제 대기"),
    ORDER_COMPLETED("결제 완료"),
    ORDER_CANCELED("주문 취소"),
    ORDER_CONFIRMED("주문 확인");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
