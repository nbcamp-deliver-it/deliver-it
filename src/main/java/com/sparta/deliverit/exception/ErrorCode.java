package com.sparta.deliverit.exception;

public enum ErrorCode {

    // Order
    ORDER_FAILED,
    DUPLICATE_ORDER,
    OUT_OF_STOCK,

    // Payment
    PAYMENT_FAILED, // 결제 실패
    PAYMENT_CANCEL, // 결제 취소
    INVALID_CARD_NUMBER, // 유효하지 않은 카드 번호
    CARD_LIMIT_EXCEEDED, // 한도 초과
    PAYMENT_GATEWAY_ERROR, // PG사 결제 에러
    INSUFFICIENT_FUNDS, // 잔액 부족
    NETWORK_TIMEOUT, // 결제 시간 초과

    // User
    REGISTER_FAILED,
    LOGIN_FAILED,

    // Client
    BAD_REQUEST,

    // Server
    SERVER_ERROR
}
