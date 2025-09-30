package com.sparta.deliverit.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderListResponse {
    @JsonProperty("orders")
    private final List<OrderInfo> orders;

    @Builder
    private OrderListResponse(List<OrderInfo> orders) {
        this.orders = orders;
    }

    public static OrderListResponse of(List<OrderInfo> orderInfos) {
        return new OrderListResponse(orderInfos);
    }
}
