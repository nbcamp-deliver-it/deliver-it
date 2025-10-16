package com.sparta.deliverit.menu.presentation.dto;

import com.sparta.deliverit.menu.domain.entity.MenuStatus;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class MenuCreateRequestDto {

    @NotNull
    private Restaurant restaurant;

    @NotNull
    private String name;

    @NotNull
    private BigDecimal price;

    @NotNull
    private MenuStatus status;

    @NotNull
    private Boolean isAiDescGenerated;

    private String description;
}
