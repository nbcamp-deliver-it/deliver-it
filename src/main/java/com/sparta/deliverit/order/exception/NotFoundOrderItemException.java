package com.sparta.deliverit.order.exception;

import com.sparta.deliverit.global.exception.OrderException;
import com.sparta.deliverit.global.response.code.OrderResponseCode;

public class NotFoundOrderItemException extends OrderException {
    public NotFoundOrderItemException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
