package com.sparta.deliverit.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateOrderResponse {
    @JsonProperty("order_info")
    private final CreateOrderInfo createOrderInfo;

    @Builder
    private CreateOrderResponse(CreateOrderInfo createOrderInfo) {
        this.createOrderInfo = createOrderInfo;
    }

    public static CreateOrderResponse of(CreateOrderInfo createOrderInfo) {
        return new CreateOrderResponse(createOrderInfo);
    }
}
