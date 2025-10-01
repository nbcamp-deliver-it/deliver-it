package com.sparta.deliverit.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public class CreateOrderInfo {
    @JsonProperty("orderId")
    private final String orderId;

    @JsonProperty("orderStatus")
    private final String orderStatus;

    @JsonProperty("totalPrice")
    private final int totalPrice;

    @Builder
    private CreateOrderInfo(String orderStatus, String orderId, int totalPrice) {
        this.orderStatus = orderStatus;
        this.orderId = orderId;
        this.totalPrice = totalPrice;
    }

    public static CreateOrderInfo create(String orderStatus, String orderId, int totalPrice) {
        return new CreateOrderInfo(orderStatus, orderId, totalPrice);
    }
}
