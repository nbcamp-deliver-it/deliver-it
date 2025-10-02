package com.sparta.deliverit.restaurant.infrastructure.repository;

import com.sparta.deliverit.restaurant.domain.entity.Category;
import com.sparta.deliverit.restaurant.domain.model.RestaurantCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Set<Category> findAllByNameIn(List<RestaurantCategory> categories);

    Optional<Category> findByName(RestaurantCategory category);
}
