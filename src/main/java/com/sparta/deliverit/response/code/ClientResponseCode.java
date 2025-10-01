package com.sparta.deliverit.response.code;

public enum ClientResponseCode implements ResponseCodeType {
    NOT_FOUND,
    BAD_REQUEST,
    MISSING_PARAMETER,
    INVALID_PATH,
    INVALID_TYPE,
    VALIDATION_ERROR,
    UNREADABLE_PARAMETER,
    BINDING_FAILED
}
