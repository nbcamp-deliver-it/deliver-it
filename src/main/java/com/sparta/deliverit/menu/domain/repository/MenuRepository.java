package com.sparta.deliverit.menu.domain.repository;

import com.sparta.deliverit.menu.domain.entity.Menu;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, String> {
    List<Menu> findByRestaurant(Restaurant restaurant);
    List<Menu> findByIdIn(Collection<String> ids);
    boolean existsByRestaurantRestaurantIdAndName(String restaurantId, String name);
}
