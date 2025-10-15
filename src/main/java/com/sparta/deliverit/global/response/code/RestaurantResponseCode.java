package com.sparta.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public enum RestaurantResponseCode implements ResponseCode {
    RESTAURANT_NOT_FOUND(NOT_FOUND, "일치하는 음식점을 찾을 수 없습니다."),
    RESTAURANT_FORBIDDEN(FORBIDDEN, "음식점 접근 권한이 없습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;

    RestaurantResponseCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return null;
    }

    @Override
    public String getMessage() {
        return "";
    }
}