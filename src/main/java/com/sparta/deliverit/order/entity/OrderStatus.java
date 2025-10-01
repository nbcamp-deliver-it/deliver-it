package com.sparta.deliverit.order.entity;

public enum OrderStatus {
    CREATED("주문 생성"),
    PAYMENT_PENDING("결제 대기"),
    PAYMENT_COMPLETED("결제 완료"),
    PAYMENT_CANCEL("결제 완료"),
    CANCELED("주문 취소"),
    CONFIRMED("주문 확인");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
