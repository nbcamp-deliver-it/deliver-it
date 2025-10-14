package com.sparta.deliverit.order.exception;

import com.sparta.deliverit.global.exception.OrderException;
import com.sparta.deliverit.global.response.code.OrderResponseCode;

public class OrderCancelFailException extends OrderException {
    public OrderCancelFailException(OrderResponseCode responseCode) {
        super(responseCode);
    }
}
