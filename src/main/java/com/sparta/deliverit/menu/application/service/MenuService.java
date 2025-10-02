package com.sparta.deliverit.menu.application.service;

import com.sparta.deliverit.menu.domain.entity.Menu;
import com.sparta.deliverit.menu.domain.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public List<Menu> getMenuByRestaurantId(String restaurantId) {
        return null;
    }

    public void createMenuItem(String restaurantId, List<Menu> menuList) {
        menuRepository.saveAll(menuList);
    }

    public void deleteMenuItem(String restaurantId, List<String> menuIdList) {
        // 로직 추가 필요
    }

    public void updateMenuItem(String restaurantId, List<Menu> menuList) {
        // 로직 추가 필요
    }
}
