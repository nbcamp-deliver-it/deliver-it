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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    // 음식점 등록
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'MASTER')")
    public ResponseEntity<RestaurantInfoResponseDto> createRestaurant(
            @Valid @RequestBody RestaurantInfoRequestDto requestDto
    ) {
        log.info("Controller - createRestaurant 실행");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.createRestaurant(requestDto));
    }
}
