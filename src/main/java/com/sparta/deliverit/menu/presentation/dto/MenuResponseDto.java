package com.sparta.deliverit.menu.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class MenuResponseDto {
    private String name;
    private BigDecimal price;
    private String description;
}
