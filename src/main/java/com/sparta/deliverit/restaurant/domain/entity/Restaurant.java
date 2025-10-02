package com.sparta.deliverit.restaurant.domain.entity;

import com.sparta.deliverit.restaurant.domain.model.RestaurantStatus;
import com.sparta.deliverit.restaurant.infrastructure.api.map.Coordinates;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantInfoRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

    public void updateCoordinates(Double longitude, Double latitude) {
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

    // 음식점 - 리뷰 1:N 관계
//    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Review> reviews = new ArrayList<>();
//
//    @Column(nullable = false)
//    @Builder.Default
//    private Long reviewsCount = 0L;
//
//    @Column(nullable = false, precision = 2, scale = 1)
//    @Builder.Default
//    private BigDecimal starAvg = BigDecimal.ZERO;
//
//    // 리뷰 등록 시 호출
//    public void applyNewReview(int rating) {
//        reviewsCount++;
//
//        BigDecimal newSum = starAvg.multiply(BigDecimal.valueOf(reviewsCount))
//                .add(BigDecimal.valueOf(rating));
//        starAvg = newSum.divide(BigDecimal.valueOf(reviewsCount), 1, HALF_UP);
//    }
//
//    // 리뷰 수정 시 호출
//    public void applyUpdatedReview(int oldRating, int newRating) {
//        if (reviewsCount == 0) return;
//
//        BigDecimal newSum = starAvg.multiply(BigDecimal.valueOf(reviewsCount))
//                .add(BigDecimal.valueOf(newRating - oldRating));
//        starAvg = newSum.divide(BigDecimal.valueOf(reviewsCount), 1, HALF_UP);
//    }
//
//    // 리뷰 삭제 시 호출
//    public void applyDeletedReview(int rating) {
//        if (reviewsCount <= 1) {
//            reviewsCount = 0L;
//            starAvg = BigDecimal.ZERO;
//            return;
//        }
//
//        reviewsCount--;
//
//        BigDecimal newSum = starAvg.multiply(BigDecimal.valueOf(this.reviewsCount))
//                .subtract(BigDecimal.valueOf(rating));
//        starAvg = newSum.divide(BigDecimal.valueOf(reviewsCount), 1, HALF_UP);
//    }

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
