package com.sparta.deliverit.restaurant.application.service;

import com.sparta.deliverit.global.exception.RestaurantException;
import com.sparta.deliverit.global.infrastructure.security.UserDetailsImpl;
import com.sparta.deliverit.global.persistence.UseActiveRestaurantFilter;
import com.sparta.deliverit.menu.application.service.MenuService;
import com.sparta.deliverit.menu.presentation.dto.MenuResponseDto;
import com.sparta.deliverit.restaurant.domain.entity.Category;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import com.sparta.deliverit.restaurant.domain.model.RestaurantCategory;
import com.sparta.deliverit.restaurant.domain.model.RestaurantStatus;
import com.sparta.deliverit.restaurant.domain.model.SortType;
import com.sparta.deliverit.restaurant.infrastructure.api.map.Coordinates;
import com.sparta.deliverit.restaurant.infrastructure.api.map.MapService;
import com.sparta.deliverit.restaurant.infrastructure.repository.CategoryRepository;
import com.sparta.deliverit.restaurant.infrastructure.repository.RestaurantRepository;
import com.sparta.deliverit.restaurant.presentation.dto.*;
import com.sparta.deliverit.user.domain.entity.User;
import com.sparta.deliverit.user.domain.repository.UserRepository;
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

import static com.sparta.deliverit.global.response.code.RestaurantResponseCode.RESTAURANT_FORBIDDEN;
import static com.sparta.deliverit.global.response.code.RestaurantResponseCode.RESTAURANT_NOT_FOUND;
import static com.sparta.deliverit.global.response.code.UserResponseCode.*;
import static com.sparta.deliverit.restaurant.domain.model.RestaurantStatus.SHUTDOWN;
import static com.sparta.deliverit.restaurant.domain.model.SortType.DISTANCE;
import static com.sparta.deliverit.restaurant.domain.model.SortType.RATING;
import static com.sparta.deliverit.user.domain.entity.UserRoleEnum.OWNER;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final MapService mapService;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final MenuService menuService;

    // 음식점 등록
    @Transactional
    public RestaurantInfoResponseDto createRestaurant(RestaurantInfoRequestDto requestDto, UserDetailsImpl user) {
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

        // 4. 음식점 소유자 정보 저장
        boolean isOwner = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(OWNER.getAuthority()));

        // 권한이 OWNER일 경우 바로 저장 / MANAGER, MASTER일 경우 음식점 소유주의 아이디를 받아와서 저장
        if (isOwner) {
            restaurant.assignUser(user.getUser());
        } else {
            User owner = userRepository.findByUsername(requestDto.getOwnerId())
                    .orElseThrow(() -> {
                        log.error("일치하는 사용자를 찾을 수 없습니다.: OwnerId={}", requestDto.getOwnerId());
                        return new RestaurantException(NOT_FOUND_USER);
                    });

            restaurant.assignUser(owner);
        }
        log.debug("음식점 소유주 저장 완료: ownerId={}", restaurant.getUser().getUsername());

        // 5. DTO 반환
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
    @UseActiveRestaurantFilter
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
    @UseActiveRestaurantFilter
    @Transactional(readOnly = true)
    public RestaurantDetailResponseDto getRestaurantDetail(String restaurantId) throws Exception {
        log.info("Service - getRestaurantInfo 실행: restaurantId={}", restaurantId);

        // 메뉴 조회
        List<MenuResponseDto> menu = menuService.getMenuByRestaurantId(restaurantId);

        // DTO 반환
        return RestaurantDetailResponseDto.from(getRestaurant(restaurantId), menu);
    }

    // 음식점 조회
    private Restaurant getRestaurant(String restaurantId) throws Exception {
        return restaurantRepository.findByRestaurantId(restaurantId)
                .orElseThrow(() -> {
                    log.error("일치하는 음식점을 찾을 수 없습니다. restaurantId={}", restaurantId);
                    return new RestaurantException(RESTAURANT_NOT_FOUND);
                });
    }

    // 음식점 수정
    @Transactional
    public RestaurantInfoResponseDto updateRestaurant(
            String restaurantId, RestaurantInfoRequestDto requestDto, UserDetailsImpl user) throws Exception {
        log.info("Service - updateRestaurant 실행: restaurantId={}", restaurantId);

        // 1. 음식점 아이디와 일치하는 음식점의 정보가 존재하는지 확인
        Restaurant restaurant = getRestaurant(restaurantId);
        log.debug("Restaurant 조회 결과: restaurantName={}", restaurant.getName());

        // 2. 음식점 영업 여부 확인
        ensureNotShutdown(restaurant);

        // 3. 권한이 OWNER일 경우, 음식점의 소유권 확인
        validateRestaurantOwner(restaurant, user);

        // 4. 엔티티에 수정 사항 반영
        // 카테고리 정보 수정
        Set<Category> categories = categoryRepository.findAllByNameIn(requestDto.getCategories());
        log.debug("카테고리 매핑: categories={}", categories);

        // 좌표 정보 수정
        Coordinates geocode = mapService.geocode(requestDto.getAddress());
        log.debug("주소 -> 좌표 변환: lon={}, lat={}", geocode.getLongitude(), geocode.getLatitude());

        // 엔티티에 수정 사항 반영
        restaurant.update(requestDto, categories, geocode);

        // 5. DTO 반환
        log.info("Service - updateRestaurant 종료: name={}", requestDto.getName());
        return RestaurantInfoResponseDto.from(restaurant);
    }

    private void ensureNotShutdown(Restaurant restaurant) {
        if (restaurant.getStatus() == SHUTDOWN) {
            log.error("폐업한 가게는 수정 및 삭제 불가능: restaurantId={}", restaurant.getRestaurantId());
            throw new RestaurantException(RESTAURANT_FORBIDDEN);
        }
    }

    // 음식점 상태 수정
    @Transactional
    public RestaurantInfoResponseDto updateRestaurantStatus(
            String restaurantId, RestaurantStatus status, UserDetailsImpl user
    ) throws Exception {
        // 1. 음식점 아이디와 일치하는 음식점의 정보가 존재하는지 확인
        Restaurant restaurant = getRestaurant(restaurantId);

        // 2. 음식점 영업 여부 확인
        ensureNotShutdown(restaurant);

        // 3. 권한이 OWNER일 경우, 음식점의 소유권 확인
        validateRestaurantOwner(restaurant, user);

        // 4. 엔티티에 수정된 상태 정보 반영
        restaurant.updateStatus(status);

        // 5. DTO 반환
        return RestaurantInfoResponseDto.from(restaurant);
    }

    // 음식점 삭제
    @Transactional
    public void deleteRestaurant(
            String restaurantId, UserDetailsImpl user) throws Exception {
        log.info("Service - deleteRestaurant 실행: restaurantId={}", restaurantId);

        // 1. 음식점 아이디와 일치하는 음식점의 정보가 존재하는지 확인
        Restaurant restaurant = getRestaurant(restaurantId);
        log.debug("Restaurant 조회 결과: restaurantName={}", restaurant.getName());

        // 2. 음식점 영업 여부 확인
        ensureNotShutdown(restaurant);

        // 3. 권한이 OWNER일 경우, 음식점의 소유권 확인
        validateRestaurantOwner(restaurant, user);

        // 4. 음식점 삭제 처리 (soft delete)
        restaurant.softDelete();
    }

    // 권한이 OWNER일 때, 음식점의 소유주와 현재 사용자가 일치하지 않을 경우 예외 반환
    private void validateRestaurantOwner(Restaurant restaurant, UserDetailsImpl userDetails) {
        log.debug("소유주 일치 확인: restaurantOwnerId={}, currentUserId={}",
                restaurant.getUser().getUsername(), userDetails.getUsername());

        boolean isOwner = userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(OWNER.getAuthority()));
        boolean isOwnerMismatch = !userDetails.getUsername().equals(restaurant.getUser().getUsername());

        if (isOwner && isOwnerMismatch) {
            log.error("음식점 소유주가 일치하지 않습니다.: restaurantOwnerId={}, currentUserId={}",
                    restaurant.getUser().getUsername(), userDetails.getUsername());
            throw new RestaurantException(RESTAURANT_FORBIDDEN);
        }
    }
}
