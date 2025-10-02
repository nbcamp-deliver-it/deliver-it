package com.sparta.deliverit.order.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderMenuRequest {

    @NotBlank(message = "메뉴의 UUID는 필수값 입니다.")
    @Size(min=36, max=36, message = "식당의 UUID는 36글자로 구성되어야 합니다.")
    @JsonProperty("menuId")
    private final String menuId;

    @Positive(message = "메뉴 수량은 자연수여야 합니다.") @Min(value = 1, message = "메뉴 수량은 1 이상이어야 합니다.")
    private final int quantity;

    @Builder
    protected OrderMenuRequest(String menuId, int quantity) {
        this.menuId = menuId;
        this.quantity = quantity;
    }
}
