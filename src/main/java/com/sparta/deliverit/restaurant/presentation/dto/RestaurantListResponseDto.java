package com.sparta.deliverit.restaurant.presentation.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RestaurantListResponseDto {
    private String restaurantId;
    private String name;
    private BigDecimal rating;
    private Long reviewCount;
    private Double distance; // 별점순 정렬 시 NULL

    // 별점순 정렬 시 사용
    public RestaurantListResponseDto(
            String restaurantId, String name, BigDecimal rating, Long reviewCount
    ) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.rating = rating;
        this.reviewCount = reviewCount;
    }
}