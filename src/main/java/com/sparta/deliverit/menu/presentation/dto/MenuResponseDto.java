package com.sparta.deliverit.menu.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MenuResponseDto {
    private String name;
    private Integer price;
    private String description;
}
