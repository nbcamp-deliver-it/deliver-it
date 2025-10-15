package com.sparta.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public enum RestaurantResponseCode implements ResponseCode{

    RESTAURANT_NOT_FOUND(BAD_REQUEST, "존재하지 않는 식당입니다.");

    private final HttpStatus httpStatus;

    private final String message;

    RestaurantResponseCode(HttpStatus httpStatus, String message) {
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
