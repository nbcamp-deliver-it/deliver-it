package com.sparta.deliverit.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MenuInfo {
    @JsonProperty("menuName")
    private final String menuName;

    @JsonProperty("quantity")
    private final int quantity;

    @JsonProperty("price")
    private final int price;

    @Builder
    private MenuInfo(String menuName, int quantity, int price) {
        this.menuName = menuName;
        this.quantity = quantity;
        this.price = price;
    }

    public static MenuInfo create(String menuName, int quantity, int price) {
        return MenuInfo.builder()
                .menuName(menuName)
                .quantity(quantity)
                .price(price)
                .build();
    }
}
