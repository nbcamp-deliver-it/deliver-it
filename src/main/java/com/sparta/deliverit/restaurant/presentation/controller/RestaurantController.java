package com.sparta.deliverit.restaurant.presentation.controller;

import com.sparta.deliverit.global.infrastructure.security.UserDetailsImpl;
import com.sparta.deliverit.restaurant.application.service.RestaurantService;
import com.sparta.deliverit.restaurant.domain.model.RestaurantStatus;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantInfoRequestDto;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantInfoResponseDto;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantListRequestDto;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantListResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    // 음식점 등록
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    public ResponseEntity<RestaurantInfoResponseDto> createRestaurant(
            @Valid @RequestBody RestaurantInfoRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        log.info("Controller - createRestaurant 실행: restaurantName={}", requestDto.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.createRestaurant(requestDto, user));
    }

    // 음식점 전체 목록 조회
    @GetMapping
    public ResponseEntity<Page<RestaurantListResponseDto>> getRestaurantList(
            @Valid @ModelAttribute RestaurantListRequestDto requestDto,
            @PageableDefault(size = 20, sort = "distance", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        log.info("Controller - getAllRestaurants 실행: latitude={}, longitude={}, keyword={}, category={}",
                requestDto.getLatitude(), requestDto.getLongitude(), requestDto.getKeyword(), requestDto.getCategory());

        Page<RestaurantListResponseDto> restaurantList = restaurantService.getRestaurantList(requestDto, pageable);

        log.info("Controller - getAllRestaurants 종료: restaurantList Total Page={}", restaurantList.getTotalPages());
        return ResponseEntity.ok(restaurantList);
    }

    // 음식점 단일 조회
    @GetMapping("/{restaurantId}")
    public ResponseEntity<RestaurantInfoResponseDto> getRestaurantInfo(@PathVariable String restaurantId) throws Exception {
        log.info("Controller - getRestaurantInfo 실행: restaurantId={}", restaurantId);

        RestaurantInfoResponseDto restaurant = restaurantService.getRestaurantInfo(restaurantId);

        log.info("Controller - getRestaurantInfo 종료: restaurantId={}", restaurant.getRestaurantId());
        return ResponseEntity.ok(restaurant);
    }

    // 음식점 수정
    @PutMapping("/{restaurantId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    public ResponseEntity<RestaurantInfoResponseDto> updateRestaurant(
            @PathVariable String restaurantId,
            @Valid @RequestBody RestaurantInfoRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl user
    ) throws Exception {
        log.info("Controller - updateRestaurant 실행: restaurantId={}, restaurantName={}", restaurantId, requestDto.getName());

        RestaurantInfoResponseDto restaurant = restaurantService.updateRestaurant(restaurantId, requestDto, user); // + userDetails

        log.info("Controller - updateRestaurant 종료: restaurantId={}, restaurantName={}", restaurant.getRestaurantId(), restaurant.getName());
        return ResponseEntity.ok(restaurant);
    }

    // 음식점 상태 수정
    @PatchMapping("/{restaurantId}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    public ResponseEntity<RestaurantInfoResponseDto> updateRestaurantStatus(
            @PathVariable String restaurantId,
            @RequestParam RestaurantStatus status,
            @AuthenticationPrincipal UserDetailsImpl user
    ) throws Exception {
        log.info("Controller - updateRestaurantStatus 실행: restaurantId={}, status={}", restaurantId, status);

        RestaurantInfoResponseDto restaurant = restaurantService.updateRestaurantStatus(restaurantId, status, user);

        log.info("Controller - updateRestaurantStatus 종료: restaurantId={}, status={}", restaurant.getRestaurantId(), restaurant.getStatus());
        return ResponseEntity.ok(restaurant);
    }

    // 음식점 삭제
    @DeleteMapping("/{restaurantId}")
    @PreAuthorize("hasAnyRole('OWNER', 'MANAGER', 'MASTER')")
    public ResponseEntity<String> deleteRestaurant(
            @PathVariable String restaurantId,
            @AuthenticationPrincipal UserDetailsImpl user
    ) throws Exception {
        log.info("Controller - deleteRestaurant 실행: restaurantId={}", restaurantId);

        restaurantService.deleteRestaurant(restaurantId, user);
        return ResponseEntity.ok("삭제가 성공적으로 완료되었습니다.");
    }
}
