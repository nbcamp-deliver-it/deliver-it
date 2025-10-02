package com.sparta.deliverit.restaurant.application.service;

import com.sparta.deliverit.restaurant.domain.entity.Category;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import com.sparta.deliverit.restaurant.infrastructure.api.map.Coordinates;
import com.sparta.deliverit.restaurant.infrastructure.api.map.MapService;
import com.sparta.deliverit.restaurant.infrastructure.repository.CategoryRepository;
import com.sparta.deliverit.restaurant.infrastructure.repository.RestaurantRepository;
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

    private final MapService mapService;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;

    // 음식점 등록
    @Transactional
    public RestaurantInfoResponseDto createRestaurant(RestaurantInfoRequestDto requestDto) {
        log.info("Service - createRestaurant 시작: name={}", requestDto.getName());

        // 1. 고유 식별자 생성 및 엔티티 변환
        Restaurant restaurant = Restaurant.from(requestDto, generateRestaurantId());
        log.debug("restaurantId 생성: restaurantId={}", restaurant.getRestaurantId().substring(0, 5));

        // 2. 카테고리 매핑
        Set<Category> categories = categoryRepository.findAllByNameIn(requestDto.getCategories());
        restaurant.assignCategories(categories);
        log.debug("카테고리 매핑: categories={}", categories);

        // 3. 주소 -> 좌표 변환 후 엔티티 업데이트
        Coordinates geocode = mapService.geocode(requestDto.getAddress());
        restaurant.updateCoordinates(geocode.getLongitude(), geocode.getLatitude());
        log.debug("주소 -> 좌표 변환: lon={}, lat={}", geocode.getLongitude(), geocode.getLatitude());

        // 4. 저장 후 DTO 반환
        restaurantRepository.save(restaurant);
        log.info("Service - createRestaurant 종료: name={}", requestDto.getName());
        return RestaurantInfoResponseDto.from(restaurant);
    }

    // 음식점 고유 식별자 생성
    private static String generateRestaurantId() {
        return UUID.randomUUID().toString();
    }
}