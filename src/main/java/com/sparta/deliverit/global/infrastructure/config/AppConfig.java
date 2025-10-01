package com.sparta.deliverit.global.infrastructure.config;

import com.sparta.deliverit.restaurant.domain.entity.Category;
import com.sparta.deliverit.restaurant.domain.model.RestaurantCategory;
import com.sparta.deliverit.restaurant.domain.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    CommandLineRunner seedCategories(CategoryRepository repository) {
        return args -> {
            for (RestaurantCategory category : RestaurantCategory.values()) {
                repository.findByName(category).orElseGet(() ->
                        repository.save(Category.builder().name(category).build())
                );
            }
        };
    }
}
