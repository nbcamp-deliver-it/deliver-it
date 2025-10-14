package com.sparta.deliverit.restaurant.presentation.dto;

import com.sparta.deliverit.restaurant.domain.model.RestaurantCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RestaurantListRequestDto {

    @NotNull
    @DecimalMin(value = "-180.0")
    @DecimalMax(value = "180.0")
    private Double longitude;

    @NotNull
    @DecimalMin(value = "-90.0")
    @DecimalMax(value = "90.0")
    private Double latitude;

    private String keyword;
    private RestaurantCategory category;

    public String getNormalizedKeyword() {
        if (keyword == null) return null;
        String t = keyword.trim();
        return t.isEmpty() ? null : t;
    }
}