package com.sparta.deliverit.menu.domain.entity;

import com.sparta.deliverit.menu.presentation.dto.MenuUpdateRequest;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_menu")
public class Menu {

    @Id
    @Column(name = "menu_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private MenuStatus status;

    @Column(columnDefinition = "TEXT")
    private String description;

    public void applyUpdate(MenuUpdateRequest req) {
        if (req.getName() != null) this.name = req.getName();
        if (req.getPrice() != null) this.price = req.getPrice();
        if (req.getStatus() != null) this.status = req.getStatus();
        if (req.getDescription() != null) this.description = req.getDescription();
    }
}
