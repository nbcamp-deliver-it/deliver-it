package com.sparta.deliverit.order.exception;

import com.sparta.deliverit.global.exception.OrderException;
import com.sparta.deliverit.global.response.code.OrderResponseCode;

public class NotFoundOrderException extends OrderException {
    public NotFoundOrderException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
