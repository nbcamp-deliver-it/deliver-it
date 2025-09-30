package com.sparta.deliverit.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderResponse {

    @JsonProperty("order")
    private final OrderInfo orderInfo;

    @Builder
    private OrderResponse(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public static OrderResponse of(OrderInfo orderInfo) {
        return new OrderResponse(orderInfo);
    }
}
