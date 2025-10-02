package com.sparta.deliverit.restaurant.presentation.controller;

import com.sparta.deliverit.restaurant.application.service.RestaurantService;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantInfoRequestDto;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantInfoResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    // 음식점 등록
    @PostMapping
//    @PreAuthorize("hasAnyRole('OWNER', 'MASTER')")
    @PreAuthorize("hasAnyAuthority('SCOPE_OWNER', 'SCOPE_MASTER')")
    public ResponseEntity<RestaurantInfoResponseDto> createRestaurant(
            @Valid @RequestBody RestaurantInfoRequestDto requestDto
    ) {
        log.info("Controller - createRestaurant 실행: restaurantName={}", requestDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.createRestaurant(requestDto));
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
//    @PreAuthorize("hasAnyRole('OWNER', 'MASTER')")
    @PreAuthorize("hasAnyAuthority('SCOPE_OWNER', 'SCOPE_MASTER')")
    public ResponseEntity<RestaurantInfoResponseDto> updateRestaurant(
            @PathVariable String restaurantId, @Valid @RequestBody RestaurantInfoRequestDto requestDto
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {
        log.info("Controller - updateRestaurant 실행: restaurantId={}, restaurantName={}", restaurantId, requestDto.getName());

        RestaurantInfoResponseDto restaurant = restaurantService.updateRestaurant(restaurantId, requestDto); // + userDetails
        return ResponseEntity.ok(restaurant);
    }

    // 음식점 삭제
    @DeleteMapping("/{restaurantId}")
//    @PreAuthorize("hasAnyRole('OWNER', 'MASTER')")
    @PreAuthorize("hasAnyAuthority('SCOPE_OWNER', 'SCOPE_MASTER')")
    public ResponseEntity<String> deleteRestaurant(@PathVariable String restaurantId
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) throws Exception {
        log.info("Controller - deleteRestaurant 실행: restaurantId={}", restaurantId);

        restaurantService.deleteRestaurant(restaurantId); // + userDetails
        return ResponseEntity.ok("삭제가 성공적으로 완료되었습니다.");
    }
}
