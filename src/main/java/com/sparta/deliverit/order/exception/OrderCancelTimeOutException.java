package com.sparta.deliverit.order.exception;

import com.sparta.deliverit.global.exception.OrderException;
import com.sparta.deliverit.global.response.code.OrderResponseCode;

public class OrderCancelTimeOutException extends OrderException {
    public OrderCancelTimeOutException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
