package com.sparta.deliverit.global.exception;

import com.sparta.deliverit.global.response.code.ResponseCode;

public class RestaurantException extends DomainException {
    public RestaurantException(ResponseCode responseCode) {
        super(responseCode);
    }
}