package com.sparta.deliverit.menu.presentation.controller;

import com.sparta.deliverit.menu.application.service.MenuService;
import com.sparta.deliverit.menu.domain.entity.Menu;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/v1/restaurants/{restaurantId}/menu")
    public ResponseEntity<List<Menu>> getMenuByRestaurantId(@PathVariable String restaurantId) {
        List<Menu> menu = menuService.getMenuByRestaurantId(restaurantId);

        return ResponseEntity.ok(menu);
    }

    @PostMapping("/v1/restaurants/{restaurantId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createMenuItem(
            @PathVariable String restaurantId,
            @RequestBody List<Menu> menu) {
        menuService.createMenuItem(restaurantId, menu);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/v1/restaurants/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMenuItem(
            @PathVariable String restaurantId,
            @RequestBody @Valid List<String> menuIdList) {
        menuService.deleteMenuItem(restaurantId, menuIdList);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/v1/restaurants/{restaurantId}")
    public ResponseEntity<Void> updateMenuItem(
            @PathVariable String restaurantId,
            @RequestBody @Valid List<Menu> menuList) {
        menuService.updateMenuItem(restaurantId, menuList);

        return ResponseEntity.noContent().build();
    }
}
