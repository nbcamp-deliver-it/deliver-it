package com.sparta.deliverit.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

public class CreateOrderInfo {
    @JsonProperty("orderId")
    private final String orderId;

    @JsonProperty("orderStatus")
    private final String orderStatus;

    @JsonProperty("totalPrice")
    private final BigDecimal totalPrice;

    @Builder
    private CreateOrderInfo(String orderStatus, String orderId, BigDecimal totalPrice) {
        this.orderStatus = orderStatus;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
    }

    public static CreateOrderInfo create(String orderStatus, String orderId, BigDecimal totalPrice) {
        return new CreateOrderInfo(orderStatus, orderId, totalPrice);
    }
}
