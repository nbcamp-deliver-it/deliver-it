package com.sparta.deliverit.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public class CreateOrderInfo {
    @JsonProperty("order_id")
    private final String orderId;

    @JsonProperty("order_status")
    private final String orderStatus;

    @JsonProperty("total_price")
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
