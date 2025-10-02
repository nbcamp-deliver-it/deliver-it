package com.sparta.deliverit.menu.application.service;

import com.sparta.deliverit.menu.domain.entity.Menu;
import com.sparta.deliverit.menu.domain.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public List<Menu> getMenuByRestaurantId(String restaurantId) {
        List<Menu> menuList = menuRepository.findAllByRestaurant_Id(restaurantId);

        return menuList.isEmpty() ? Collections.emptyList() : menuList;
    }

    @Transactional
    public void createMenuItem(String restaurantId, List<Menu> menuList) {
//        Restaurant restaurant = restaurantRepository.findById(restaurantId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 음식점이 존재하지 않습니다."));
//
//        for (Menu menu : menuList) {
//            menu.setRestaurant(restaurant); // FK 세팅
//        }

        menuRepository.saveAll(menuList);
    }

    @Transactional
    public void deleteMenuItem(String restaurantId, List<String> menuIdList) {

    }

    @Transactional
    public void updateMenuItem(String restaurantId, List<Menu> menuList) {
        for (Menu updatedMenu : menuList) {
            Menu existingMenu = menuRepository
                    .findById(updatedMenu.getId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메뉴입니다."));

            String id = existingMenu.getRestaurant().getRestaurantId();

            if (id.equals(restaurantId)) {
                throw new IllegalArgumentException("해당 음식점의 메뉴가 아닙니다.");
            }

            existingMenu.updateMenu(updatedMenu);

            //  @Transactional dirty checking 으로 Menu 자동 업데이트
        }
    }
}
