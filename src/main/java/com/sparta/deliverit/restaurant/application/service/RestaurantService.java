package com.sparta.deliverit.restaurant.application.service;

import com.sparta.deliverit.restaurant.domain.entity.Category;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import com.sparta.deliverit.restaurant.domain.model.RestaurantCategory;
import com.sparta.deliverit.restaurant.domain.model.RestaurantStatus;
import com.sparta.deliverit.restaurant.domain.model.SortType;
import com.sparta.deliverit.restaurant.infrastructure.api.map.Coordinates;
import com.sparta.deliverit.restaurant.infrastructure.api.map.MapService;
import com.sparta.deliverit.restaurant.infrastructure.repository.CategoryRepository;
import com.sparta.deliverit.restaurant.infrastructure.repository.RestaurantRepository;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantInfoRequestDto;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantInfoResponseDto;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantListRequestDto;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantListResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.sparta.deliverit.restaurant.domain.model.SortType.DISTANCE;
import static com.sparta.deliverit.restaurant.domain.model.SortType.RATING;

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

    private static final String SORT_DISTANCE = "distance";
    private static final String SORT_RATING = "rating";

    // 음식점 전체 목록 조회
    @Transactional(readOnly = true)
    public Page<RestaurantListResponseDto> getRestaurantList(
            RestaurantListRequestDto requestDto, Pageable pageable
    ) {
        log.info("Service - getRestaurantList 시작: latitude={}, longitude={}, keyword={}, category={}",
                requestDto.getLatitude(), requestDto.getLongitude(), requestDto.getKeyword(), requestDto.getCategory());

        double latitude = requestDto.getLatitude();
        double longitude = requestDto.getLongitude();
        String keyword = requestDto.getNormalizedKeyword();
        RestaurantCategory category = requestDto.getCategory();

        Pageable filtered = sanitizeSort(pageable, Set.of(SORT_DISTANCE, SORT_RATING));
        SortType type = resolveSortType(filtered);

        log.debug("음식점 전체 목록 조회: page={}, sort={}", pageable.getPageNumber(), type);

        return switch (type) {
            case DISTANCE -> {
                Pageable p = forceDistanceAscFirst(filtered);
                log.debug("distance sort={}", p.getSort());

                yield restaurantRepository.searchOrderByDistance(latitude, longitude, keyword, category, p);
            }

            case RATING -> {
                Pageable p = keepOnlyRatingOrDefault(filtered);
                log.debug("rating sort={}", p.getSort());

                yield restaurantRepository.searchByRating(keyword, category, p);
            }
        };
    }

    // pageable sort 문자열 -> enum 매핑
    private SortType resolveSortType(Pageable pageable) {
        boolean hasDistance = pageable.getSort().stream()
                .anyMatch(o -> o.getProperty().equalsIgnoreCase(SORT_DISTANCE));
        boolean hasRating = pageable.getSort().stream()
                .anyMatch(o -> o.getProperty().equalsIgnoreCase(SORT_RATING));

        if (hasDistance) return DISTANCE;
        if (hasRating) return RATING;

        return DISTANCE;
    }

    // 거리순, 별점순 정렬 외 조건 제한 및 페이지 설정
    private Pageable sanitizeSort(Pageable pageable, Set<String> whitelist) {
        List<Sort.Order> keep = pageable.getSort().stream()
                .filter(o -> whitelist.contains(o.getProperty()))
                .toList();

        Sort sort = keep.isEmpty() ? Sort.by(Sort.Order.asc(SORT_DISTANCE)) : Sort.by(keep);

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    // 거리순(ASC) 정렬 페이지 설정
    private Pageable forceDistanceAscFirst(Pageable pageable) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(Sort.Order.asc(SORT_DISTANCE));

        pageable.getSort().forEach(o -> {
            if (!o.getProperty().equalsIgnoreCase(SORT_DISTANCE)) orders.add(o);
        });

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
    }

    // 별점순(DESC(default), ASC) 정렬 페이지 설정
    private Pageable keepOnlyRatingOrDefault(Pageable pageable) {
        List<Sort.Order> ratingOnly = pageable.getSort().stream()
                .filter(o -> o.getProperty().equalsIgnoreCase(SORT_RATING))
                .toList();

        Sort sort = ratingOnly.isEmpty() ? Sort.by(Sort.Order.desc(SORT_RATING)) : Sort.by(ratingOnly);

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    // 음식점 단일 조회
    @Transactional(readOnly = true)
    public RestaurantInfoResponseDto getRestaurantInfo(String restaurantId) throws Exception {
        log.info("Service - getRestaurantInfo 실행: restaurantId={}", restaurantId);

        // 조회 후 DTO 반환
        return RestaurantInfoResponseDto.from(getRestaurant(restaurantId));
    }

    // 음식점 조회
    // 예외의 경우 추후 커스텀 에러로 변경
    private Restaurant getRestaurant(String restaurantId) throws Exception {
        // 삭제되지 않은 음식점 중 id와 일치하는 음식점이 있으면 반환, 없으면 예외 발생
        return restaurantRepository.findByRestaurantId(restaurantId)
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

    // 음식점 상태 수정
    @Transactional
    public RestaurantInfoResponseDto updateRestaurantStatus(String restaurantId, RestaurantStatus status) throws Exception {
        Restaurant restaurant = getRestaurant(restaurantId);
        restaurant.updateStatus(status);

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