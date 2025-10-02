package com.sparta.deliverit.menu.domain.repository;

import com.sparta.deliverit.menu.domain.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, String> {
    List<Menu> findAllByRestaurant_Id(String restaurantId);
}
