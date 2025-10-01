package com.sparta.deliverit.global.response.code;

public enum PaymentResponseCode implements ResponseCodeType{
    PAYMENT_SUCCESS, // 결제 성공
    PAYMENT_FAILED, // 결제 실패
    PAYMENT_CANCEL, // 결제 취소
    INVALID_CARD_NUMBER, // 유효하지 않은 카드 번호
    CARD_LIMIT_EXCEEDED, // 한도 초과
    PAYMENT_GATEWAY_ERROR, // PG사 결제 에러
    INSUFFICIENT_FUNDS, // 잔액 부족
    NETWORK_TIMEOUT, // 네트워크 시간 초과
}
