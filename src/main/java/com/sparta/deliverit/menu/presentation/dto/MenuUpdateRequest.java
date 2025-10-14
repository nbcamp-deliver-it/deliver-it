package com.sparta.deliverit.menu.presentation.dto;

import com.sparta.deliverit.menu.domain.entity.MenuStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MenuUpdateRequest {
    private String id;
    private String name;
    private BigDecimal price;
    private String description;
    private MenuStatus status;
}
