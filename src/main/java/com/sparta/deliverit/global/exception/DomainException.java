package com.sparta.deliverit.global.exception;

public class DomainException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorMessage;

    public DomainException(ErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
