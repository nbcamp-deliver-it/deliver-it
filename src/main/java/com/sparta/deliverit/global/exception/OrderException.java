package com.sparta.deliverit.global.exception;

import com.sparta.deliverit.global.response.code.OrderResponseCode;

public class OrderException extends DomainException {
    public OrderException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
