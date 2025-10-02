package com.sparta.deliverit.restaurant.application.service;

import com.sparta.deliverit.restaurant.domain.entity.Category;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import com.sparta.deliverit.restaurant.infrastructure.api.map.Coordinates;
import com.sparta.deliverit.restaurant.infrastructure.api.map.MapService;
import com.sparta.deliverit.restaurant.infrastructure.repository.CategoryRepository;
import com.sparta.deliverit.restaurant.infrastructure.repository.RestaurantRepository;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantInfoRequestDto;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantInfoResponseDto;
import jakarta.persistence.EntityNotFoundException;
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

    // 음식점 단일 조회
    public RestaurantInfoResponseDto getRestaurantInfo(String restaurantId) throws Exception {
        log.info("Service - getRestaurantInfo 실행: restaurantId={}", restaurantId);

        // 조회 후 DTO 반환
        return RestaurantInfoResponseDto.from(getRestaurant(restaurantId));
    }

    // 음식점 조회
    // 예외의 경우 추후 커스텀 에러로 변경
    private Restaurant getRestaurant(String restaurantId) throws Exception {
        // 삭제되지 않은 음식점 중 id와 일치하는 음식점이 있으면 반환, 없으면 예외 발생
        return restaurantRepository.findByRestaurantIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> {
                    log.error("일치하는 음식점을 찾을 수 없습니다. restaurantId={}", restaurantId);
                    return new EntityNotFoundException();
                });
    }

    // 음식점 수정
    @Transactional
    public RestaurantInfoResponseDto updateRestaurant(
            String restaurantId, RestaurantInfoRequestDto requestDto // , UserDetailsImpl userDetails
    ) throws Exception {
        log.info("Service - updateRestaurant 실행: restaurantId={}", restaurantId);

        // 1. 음식점 아이디와 일치하는 음식점의 정보가 존재하는지 확인
        Restaurant restaurant = getRestaurant(restaurantId);
        log.debug("Restaurant 조회 결과: restaurantName={}", restaurant.getName());

        // 2. 권한이 OWNER일 경우, 음식점의 소유권 확인
//        validateRestaurantOwner();
//        log.debug("소유자 일치 확인: currentUserId={}", userdetails.getUsername());

        // 3. 엔티티에 수정 사항 반영
        Set<Category> categories = categoryRepository.findAllByNameIn(requestDto.getCategories());
        log.debug("카테고리 매핑: categories={}", categories);

        Coordinates geocode = mapService.geocode(requestDto.getAddress());
        log.debug("주소 -> 좌표 변환: lon={}, lat={}", geocode.getLongitude(), geocode.getLatitude());

        // 수정 및 DTO 반환
        restaurant.update(requestDto, categories, geocode);
        log.info("Service - updateRestaurant 종료: name={}", requestDto.getName());
        return RestaurantInfoResponseDto.from(restaurant);
    }

    // 음식점 삭제
    @Transactional
    public void deleteRestaurant(
            String restaurantId // , UserDetailsImpl userDetails
    ) throws Exception {
        log.info("Service - deleteRestaurant 실행: restaurantId={}", restaurantId);

        // 1. 음식점 아이디와 일치하는 음식점의 정보가 존재하는지 확인
        Restaurant restaurant = getRestaurant(restaurantId);
        log.debug("Restaurant 조회 결과: restaurantName={}", restaurant.getName());

        // 2. 권한이 OWNER일 경우, 음식점의 소유권 확인
//        validateRestaurantOwner();
//        log.debug("소유자 일치 확인: currentUserId={}", userdetails.getUsername());

        // 2. 음식점 삭제 처리 (soft delete)
        restaurant.softDelete();
    }

    // 권한이 OWNER일 때, 음식점의 소유주와 현재 사용자가 일치하지 않을 경우 예외 반환
//    private void validateRestaurantOwner(Restaurant restaurant, UserDetailsImpl userDetails) {
//        boolean isOwner = userDetails.getAuthorities().stream()
//                .anyMatch(auth -> auth.getAuthority().equals(ROLE_OWNER));
//        boolean isOwnerMismatch = !userDetails.getUsername().equals(restaurant.getUser().getUsername());
//
//        if (isOwner && isOwnerMismatch) {
//            log.error("음식점 소유주가 일치하지 않습니다.: restaurantOwnerId={}, currentUserId={}", restaurant.getUser().getUsername(), userDetails.getUsername());
//            throw new AccessDeniedException();
//        }
//    }
}