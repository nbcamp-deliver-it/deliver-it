package com.sparta.deliverit.menu.domain.entity;

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

    public void updateMenu(Menu menu) {
        if (menu.getName() != null) this.name = menu.getName();
        if (menu.getPrice() != null) this.price = menu.getPrice();
        if (menu.getStatus() != null) this.status = menu.getStatus();
        if (menu.getDescription() != null) this.description = menu.getDescription();
    }
}
