package com.sparta.deliverit.global.exception;

import com.sparta.deliverit.global.response.code.ResponseCode;

public class PaymentException extends DomainException{
    public PaymentException(ResponseCode responseCode) {
        super(responseCode);
    }
}
