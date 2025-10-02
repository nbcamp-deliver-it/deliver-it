package com.sparta.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

public enum UserResponseCode implements ResponseCode {

    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "권한이 없는 유저입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    UserResponseCode(HttpStatus httpStatus, String message) {
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
