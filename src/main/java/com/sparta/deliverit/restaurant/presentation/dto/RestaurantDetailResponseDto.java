package com.sparta.deliverit.restaurant.presentation.dto;

import com.sparta.deliverit.menu.domain.entity.Menu;
import com.sparta.deliverit.menu.presentation.dto.MenuResponseDto;
import com.sparta.deliverit.restaurant.domain.entity.Category;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import com.sparta.deliverit.restaurant.domain.model.RestaurantCategory;
import com.sparta.deliverit.restaurant.domain.model.RestaurantStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RestaurantDetailResponseDto {

    private String restaurantId;
    private String name;
    private String phone;
    private String address;
    private String description;
    private RestaurantStatus status;
    private List<RestaurantCategory> categories;
    private BigDecimal starAvg;
    private Long reviewCount;
    private List<MenuResponseDto> menus;

    // Entity -> DTO 변환 팩토리 메서드
    public static RestaurantDetailResponseDto from(Restaurant restaurant, List<MenuResponseDto> menu) {
        return RestaurantDetailResponseDto.builder()
                .restaurantId(restaurant.getRestaurantId())
                .name(restaurant.getName())
                .phone(restaurant.getPhone())
                .address(restaurant.getAddress())
                .description(restaurant.getDescription())
                .status(restaurant.getStatus())
                .categories(restaurant.getCategories().stream()
                        .map(Category::getName)
                        .toList()
                )
                .starAvg(restaurant.getRating().getStarAvg())
                .reviewCount(restaurant.getRating().getReviewsCount())
                .menus(menu)
                .build();
    }
}
