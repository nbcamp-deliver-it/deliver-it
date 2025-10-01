package com.sparta.deliverit.global.response.code;

public enum ClientResponseCode implements ResponseCodeType {
    NOT_FOUND,
    BAD_REQUEST,
    MISSING_PARAMETER,
    INVALID_PATH,
    INVALID_TYPE,
    VALIDATION_FAILED,
    UNREADABLE_MESSAGE,
    BINDING_FAILED
}
