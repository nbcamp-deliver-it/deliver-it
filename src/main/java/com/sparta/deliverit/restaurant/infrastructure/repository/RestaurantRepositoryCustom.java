package com.sparta.deliverit.restaurant.infrastructure.repository;

import com.sparta.deliverit.restaurant.domain.model.RestaurantCategory;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantRepositoryCustom {
    Page<RestaurantListResponseDto> searchOrderByDistance(
            double latitude, double longitude, String keyword, RestaurantCategory category, Pageable pageable
    );

    Page<RestaurantListResponseDto> searchByRating(
            String keyword, RestaurantCategory category, Pageable pageable
    );
}