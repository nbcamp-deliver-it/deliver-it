package com.sparta.deliverit.order.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
public class CreateOrderRequest {

    @NotBlank(message = "음식점의 UUID는 필수값 입니다.") @Size(min=36, max=36, message = "음식점의 UUID는 36글자로 구성되어야 합니다.")
    @JsonProperty("restaurant_id")
    private final String restaurantId;

    @NotEmpty(message = "메뉴가 적어도 한 개 이상 존재해야 합니다. ")
    private final  List<OrderMenuRequest> menus;

    @NotBlank(message = "배송지는 필수 값입니다.")
    @JsonProperty("delivery_address")
    private final String deliveryAddress;

    @Builder
    public CreateOrderRequest(List<OrderMenuRequest> menus, String restaurantId, String deliveryAddress) {
        this.menus = menus;
        this.restaurantId = restaurantId;
        this.deliveryAddress = deliveryAddress;
    }
}
