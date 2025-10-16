package com.sparta.deliverit.restaurant.infrastructure.repository;

import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
    @EntityGraph(attributePaths = "categories")
    Optional<Restaurant> findByRestaurantIdAndDeletedFalse(String restaurantId);
    Boolean existsByRestaurantId(String restaurantId);
}