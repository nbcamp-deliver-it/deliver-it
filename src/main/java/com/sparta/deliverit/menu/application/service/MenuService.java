package com.sparta.deliverit.menu.application.service;

import com.sparta.deliverit.ai.application.GeminiMenuDescriptionServiceImpl;
import com.sparta.deliverit.global.exception.AiException;
import com.sparta.deliverit.global.exception.MenuException;
import com.sparta.deliverit.global.exception.RestaurantException;
import com.sparta.deliverit.menu.domain.entity.Menu;
import com.sparta.deliverit.menu.domain.repository.MenuRepository;
import com.sparta.deliverit.menu.presentation.dto.MenuCreateRequestDto;
import com.sparta.deliverit.menu.presentation.dto.MenuResponseDto;
import com.sparta.deliverit.menu.presentation.dto.MenuUpdateRequestDto;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import com.sparta.deliverit.restaurant.infrastructure.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sparta.deliverit.global.response.code.MenuResponseCode.*;
import static com.sparta.deliverit.global.response.code.RestaurantResponseCode.RESTAURANT_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final GeminiMenuDescriptionServiceImpl geminiMenuDescriptionService;

    public List<MenuResponseDto> getMenuByRestaurantId(String restaurantId) {
        Restaurant restaurant = findRestaurantOrThrow(restaurantId);

        List<Menu> menuList = menuRepository.findByRestaurant(restaurant);

        return menuList.stream().map(Menu::toResponseDto).toList();
    }

    public void createMenuItem(String restaurantId, List<MenuCreateRequestDto> requestDtoList) {
        validateNonEmptyList(requestDtoList);

        findRestaurantOrThrow(restaurantId);

        checkDuplicateMenuNames(restaurantId, requestDtoList);

        List<Menu> menuList = requestDtoList.stream()
                .map(dto -> {
                    if (Boolean.TRUE.equals(dto.getIsAiDescGenerated())) {
                        try {
                            String aiResult = geminiMenuDescriptionService.askQuestionToAi(dto.getPrompt());
                            dto.setDescription(aiResult);
                        } catch (AiException e) {
                            log.error("AI 설명 생성 실패 (prompt: {}): {}", dto.getPrompt(), e.getMessage());
                        } catch (Exception e) {
                            log.error("예상치 못한 오류 발생: {}", e.getMessage());
                        }
                    }
                    return Menu.from(dto);
                })
                .toList();

        menuRepository.saveAll(menuList);
    }

    public void deleteMenuItem(String restaurantId, List<String> menuIdList) {
        validateNonEmptyList(menuIdList);

        findRestaurantOrThrow(restaurantId);

        List<Menu> foundMenuList = findMenusOrThrow(menuIdList);

        validateMenuBelongsToRestaurant(restaurantId, foundMenuList);

        menuRepository.deleteAll(foundMenuList);
    }

    public void updateMenuItem(String restaurantId, List<MenuUpdateRequestDto> menuList) {
        validateNonEmptyList(menuList);

        findRestaurantOrThrow(restaurantId);

        for (MenuUpdateRequestDto req : menuList) {
            Menu existingMenu = menuRepository.findById(req.getId())
                    .orElseThrow(() -> new MenuException(MENU_NOT_FOUND)); // 메뉴 수정 중 하나라도 없으면 전체 요청 실패로 처리

            if (!existingMenu.getRestaurant().getRestaurantId().equals(restaurantId)) {
                throw new MenuException(MENU_NOT_IN_RESTAURANT);
            }

            existingMenu.applyUpdate(req);
        }
    }

    private Restaurant findRestaurantOrThrow(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantException(RESTAURANT_NOT_FOUND));
    }

    private void checkDuplicateMenuNames(String restaurantId, List<MenuCreateRequestDto> requestDtoList) {
        for (MenuCreateRequestDto requestDto : requestDtoList) {
            if (menuRepository.existsByRestaurantIdAndName(restaurantId, requestDto.getName())) {
                throw new MenuException(MENU_DUPLICATED);
            }
        }
    }

    private <T> void validateNonEmptyList(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new MenuException(REQUEST_EMPTY_LIST);
        }
    }

    private static void validateMenuBelongsToRestaurant(String restaurantId, List<Menu> foundMenuList) {
        for (Menu menu : foundMenuList) {
            if (!menu.getRestaurant().getRestaurantId().equals(restaurantId)) {
                throw new MenuException(MENU_NOT_FOUND);
            }
        }
    }

    private List<Menu> findMenusOrThrow(List<String> menuIdList) {
        List<Menu> foundMenuList = menuRepository.findAllById(menuIdList);

        if (foundMenuList.size() != menuIdList.size()) {
            throw new MenuException(MENU_NOT_FOUND);
        }

        return foundMenuList;
    }
}
