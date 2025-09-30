package com.sparta.deliverit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;

    @Override
    public String toString() {
        return "ErrorResponse {" +
                "errorCode=" + errorCode +
                ", message='" + message + '\'' +
                '}';
    }
}
