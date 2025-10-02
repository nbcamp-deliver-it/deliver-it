package com.sparta.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum OrderResponseCode implements ResponseCode {

    ORDER_SUCCESS(OK, "주문에 성공했습니다."),

    ORDER_FAILED(BAD_REQUEST, "주문에 실패했습니다."), // 세분화 필요
    DUPLICATE_ORDER(BAD_REQUEST, "중복 주문입니다."),
    OUT_OF_STOCK(CONFLICT, "재고 부족으로 주문에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    OrderResponseCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
