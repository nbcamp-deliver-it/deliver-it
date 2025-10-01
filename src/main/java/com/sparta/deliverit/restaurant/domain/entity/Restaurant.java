package com.sparta.deliverit.restaurant.domain.entity;

import com.sparta.deliverit.restaurant.domain.model.RestaurantStatus;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantInfoRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "p_restaurant")
public class Restaurant {

    @Id
    private String restaurantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double latitude;

    public void updateCoordinate(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestaurantStatus status;

    @Column(nullable = false)
    private boolean deleted;

//    // 사용자 - 음식점 1:N 관계
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    // 음식점 - 카테고리 N:M 관계
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "restaurant_category",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();

    // 리뷰 및 별점 반영 -> 스케쥴러 사용 예정
    @Column(nullable = false)
    @Builder.Default
    private Long reviewsCount = 0L;

    @Column(nullable = false, precision = 2, scale = 1)
    @Builder.Default
    private BigDecimal starAvg = BigDecimal.ZERO;

    public void updateStar(Long reviewsCount, BigDecimal starAvg) {
        this.reviewsCount = reviewsCount;
        this.starAvg = starAvg;
    }

    // 음식점 수정 메서드
    public void update(RestaurantInfoRequestDto requestDto, Set<Category> categories) {
        name = requestDto.getName();
        phone = requestDto.getPhone();
        address = requestDto.getAddress();
        description = requestDto.getDescription();
        status = requestDto.getStatus();
        this.categories.clear();
        this.categories.addAll(categories);
    }

    // 음식점 삭제 메서드 (soft delete)
    public void softDelete() {
        deleted = true;
    }

    // DTO -> Entity 변환 팩토리 메서드
    public static Restaurant from(RestaurantInfoRequestDto requestDto, String restaurantId) {
        return Restaurant.builder()
                .restaurantId(restaurantId)
                .name(requestDto.getName())
                .phone(requestDto.getPhone())
                .address(requestDto.getAddress())
                .description(requestDto.getDescription())
                .status(requestDto.getStatus())
                .build();
    }
}
