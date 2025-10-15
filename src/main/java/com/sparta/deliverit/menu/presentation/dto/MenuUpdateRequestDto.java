package com.sparta.deliverit.menu.presentation.dto;

import com.sparta.deliverit.menu.domain.entity.MenuStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuUpdateRequestDto {
    @NotBlank
    private String id;

    @NotBlank
    private String name;

    @NotNull
    private Integer price;

    @NotNull
    private MenuStatus status;

    private String description;
}
