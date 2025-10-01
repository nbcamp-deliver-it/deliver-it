package com.sparta.deliverit.restaurant.application.service;

import com.sparta.deliverit.restaurant.domain.entity.Category;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import com.sparta.deliverit.restaurant.domain.repository.CategoryRepository;
import com.sparta.deliverit.restaurant.domain.repository.RestaurantRepository;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantInfoRequestDto;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;

    // 음식점 등록
    @Transactional
    public RestaurantInfoResponseDto createRestaurant(RestaurantInfoRequestDto requestDto) {
        // 고유 식별자 생성
        String restaurantId = generateRestaurantId();

        // 음식점 정보 저장
        Restaurant restaurant = Restaurant.from(requestDto, restaurantId);

        // 카테고리
        Set<Category> categories = categoryRepository.findAllByNameIn(requestDto.getCategories());
        restaurant.getCategories().addAll(categories);

        restaurantRepository.save(restaurant);

        // 저장 결과 Entity -> DTO 변환 후 반환
        return RestaurantInfoResponseDto.from(restaurant);
    }

    // 음식점 고유 식별자 생성
    private static String generateRestaurantId() {
        return UUID.randomUUID().toString();
    }
}