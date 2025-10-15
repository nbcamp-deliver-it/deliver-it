package com.sparta.deliverit.global.response.code;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum UserResponseCode implements ResponseCode {
    USER_QUERY_SUCCESS(OK, "유저 조회에 성공하였습니다."),
    UNAUTHORIZED_USER(UNAUTHORIZED, "권한이 없는 유저입니다."),
    NOT_FOUND_USER(BAD_REQUEST, "존재하지 않는 유저입니다.");

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
