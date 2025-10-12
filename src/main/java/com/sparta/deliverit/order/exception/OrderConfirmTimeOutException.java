package com.sparta.deliverit.order.exception;

import com.sparta.deliverit.global.exception.OrderException;
import com.sparta.deliverit.global.response.code.OrderResponseCode;

public class OrderConfirmTimeOutException extends OrderException {
    public OrderConfirmTimeOutException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
