package com.sparta.deliverit.restaurant.domain.entity;

import com.sparta.deliverit.restaurant.domain.model.RestaurantCategory;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "p_category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RestaurantCategory name;
}