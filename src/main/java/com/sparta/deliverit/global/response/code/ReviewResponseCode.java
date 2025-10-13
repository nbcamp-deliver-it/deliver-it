package com.sparta.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ReviewResponseCode implements ResponseCode {
    NOT_FOUND_ORDER_REVIEW(BAD_REQUEST, "해당 주문 리뷰를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ReviewResponseCode(HttpStatus httpStatus, String message) {
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
