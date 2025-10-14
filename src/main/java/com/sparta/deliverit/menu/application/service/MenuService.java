package com.sparta.deliverit.menu.application.service;

import com.sparta.deliverit.global.exception.MenuException;
import com.sparta.deliverit.global.exception.RestaurantException;
import com.sparta.deliverit.menu.domain.entity.Menu;
import com.sparta.deliverit.menu.domain.repository.MenuRepository;
import com.sparta.deliverit.menu.presentation.dto.MenuUpdateRequest;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import com.sparta.deliverit.restaurant.infrastructure.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sparta.deliverit.global.response.code.MenuResponseCode.*;
import static com.sparta.deliverit.global.response.code.RestaurantResponseCode.RESTAURANT_NOT_FOUND;


@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;

    public List<Menu> getMenuByRestaurantId(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException(RESTAURANT_NOT_FOUND));

        return menuRepository.findByRestaurant_Id(restaurant);
    }

    public void createMenuItem(String restaurantId, List<Menu> menuList) {
        if (menuList == null || menuList.isEmpty()) {
            throw new MenuException(REQUEST_EMPTY_LIST);
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException(RESTAURANT_NOT_FOUND));

        for (Menu menu : menuList) {
            if (menuRepository.findById(menu.getId()).isPresent()) {
                throw new MenuException(MENU_DUPLICATED);
            }

            menu.setRestaurant(restaurant);
        }

        menuRepository.saveAll(menuList);
    }

    public void deleteMenuItem(String restaurantId, List<String> menuIdList) {
        if (menuIdList == null || menuIdList.isEmpty()) {
            throw new MenuException(REQUEST_EMPTY_LIST);
        }

        if (restaurantRepository.findById(restaurantId).isEmpty()) {
            throw new MenuException(MENU_NOT_FOUND);
        }

        for (String menuId : menuIdList) {
            if (menuRepository.findById(menuId).isEmpty()) {
                throw new MenuException(MENU_NOT_FOUND);
            }
        }

        List<Menu> foundMenuList = menuRepository.findAllById(menuIdList);

        for (Menu menu : foundMenuList) {
            if (!menu.getRestaurant().getRestaurantId().equals(restaurantId)) {
                throw new MenuException(MENU_NOT_FOUND);
            }
        }

        menuRepository.deleteAll(foundMenuList);
    }

    public void updateMenuItem(String restaurantId, List<MenuUpdateRequest> menuList) {
        if (menuList == null || menuList.isEmpty()) {
            throw new MenuException(REQUEST_EMPTY_LIST);
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException(RESTAURANT_NOT_FOUND));

        for (MenuUpdateRequest req : menuList) {
            Menu existingMenu = menuRepository.findById(req.getId())
                    .orElseThrow(() -> new MenuException(MENU_NOT_FOUND)); // 메뉴 수정 중 하나라도 없으면 전체 요청 실패로 처리

            if (!existingMenu.getRestaurant().getRestaurantId().equals(restaurantId)) {
                throw new MenuException(MENU_NOT_IN_RESTAURANT);
            }

            if (req.getName() != null) existingMenu.setName(req.getName());
            if (req.getPrice() != null) existingMenu.setPrice(req.getPrice());
            if (req.getDescription() != null) existingMenu.setDescription(req.getDescription());
            if (req.getStatus() != null) existingMenu.setStatus(req.getStatus());
        }
    }
}
