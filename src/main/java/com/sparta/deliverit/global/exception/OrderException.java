package com.sparta.deliverit.global.exception;

import com.sparta.deliverit.global.response.code.ResponseCode;

public class OrderException extends DomainException {
    public OrderException(ResponseCode responseCode) {
        super(responseCode);
    }
}
