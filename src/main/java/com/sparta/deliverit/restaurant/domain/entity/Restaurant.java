package com.sparta.deliverit.restaurant.domain.entity;

import com.sparta.deliverit.anything.entity.BaseEntity;
import com.sparta.deliverit.restaurant.domain.model.RestaurantStatus;
import com.sparta.deliverit.restaurant.domain.vo.RestaurantRating;
import com.sparta.deliverit.restaurant.infrastructure.api.map.Coordinates;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantInfoRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;

import java.util.HashSet;
import java.util.Set;

import static com.sparta.deliverit.restaurant.domain.model.RestaurantStatus.SHUTDOWN;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "p_restaurant")
@FilterDef(name = "activeRestaurantFilter")
@Filter(
        name = "activeRestaurantFilter",
        condition = "deleted_at IS NULL and status <> 'SHUTDOWN'"
)
public class Restaurant extends BaseEntity {

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

    public void updateCoordinates(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestaurantStatus status;

    public void updateStatus(RestaurantStatus status) {
        this.status = status;
    }

    // 사용자 - 음식점 1:N 관계
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

    public void assignCategories(Set<Category> categories) {
        this.categories.addAll(categories);
    }

    @Embedded
    @Builder.Default
    private RestaurantRating rating = new RestaurantRating();

    // 음식점 수정 메서드
    public void update(RestaurantInfoRequestDto requestDto, Set<Category> categories, Coordinates coordinates) {
        name = requestDto.getName();
        phone = requestDto.getPhone();
        address = requestDto.getAddress();
        longitude = coordinates.getLongitude();
        latitude = coordinates.getLatitude();
        description = requestDto.getDescription();
        status = requestDto.getStatus();
        this.categories.clear();
        this.categories.addAll(categories);
    }

    // 음식점 삭제 메서드 (soft delete)
    public void softDelete() {
        status = SHUTDOWN;
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
