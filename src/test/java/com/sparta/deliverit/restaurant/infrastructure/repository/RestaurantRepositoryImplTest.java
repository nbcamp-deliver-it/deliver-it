package com.sparta.deliverit.restaurant.infrastructure.repository;

import com.sparta.deliverit.anything.config.AuditingConfig;
import com.sparta.deliverit.anything.config.QuerydslConfig;
import com.sparta.deliverit.restaurant.domain.entity.Category;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import com.sparta.deliverit.restaurant.domain.vo.RestaurantRating;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantListResponseDto;
import com.sparta.deliverit.review.domain.vo.Star;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;

import static com.sparta.deliverit.restaurant.domain.model.RestaurantCategory.JAPANESE_FOOD;
import static com.sparta.deliverit.restaurant.domain.model.RestaurantCategory.KOREAN_FOOD;
import static com.sparta.deliverit.restaurant.domain.model.RestaurantStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = true)
@Import({QuerydslConfig.class, AuditingConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class RestaurantRepositoryImplTest {

    private static final Logger log = LoggerFactory.getLogger(RestaurantRepositoryImplTest.class);

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    EntityManager em;

    private static RestaurantRating rr(String avg, long cnt) {
        return new RestaurantRating(new BigDecimal(avg).setScale(Star.SCALE, RoundingMode.DOWN), cnt);
    }

    @BeforeEach
    void seed() {
        // 필터 활성화
        em.unwrap(Session.class)
                .enableFilter("activeRestaurantFilter");

        // 카테고리
        Category korean = categoryRepository.save(Category.builder()
                .name(KOREAN_FOOD)
                .build());

        Category japanese = categoryRepository.save(Category.builder()
                .name(JAPANESE_FOOD)
                .build());

        // 정상 데이터
        Restaurant r1 = Restaurant.builder()
                .restaurantId("R1")
                .name("한식집")
                .phone("02-0000-0001")
                .address("서울시청")
                .longitude(126.9779)
                .latitude(37.5663)
                .description("시청 근처 한식집")
                .status(OPEN)
                .rating(rr("4.5", 12))
                .categories(new HashSet<>(List.of(korean)))
                .build();

        Restaurant r2 = Restaurant.builder()
                .restaurantId("R2")
                .name("일식집")
                .phone("02-0000-0002")
                .address("광화문")
                .longitude(126.9769)
                .latitude(37.5700)
                .description("광화문 근처 일식집")
                .status(OPEN)
                .rating(rr("4.2", 30))
                .categories(new HashSet<>(List.of(japanese)))
                .build();

        Restaurant r3 = Restaurant.builder()
                .restaurantId("R3")
                .name("강남한식")
                .phone("02-0000-0003")
                .address("강남역")
                .longitude(127.0276)
                .latitude(37.4979)
                .description("강남 근처 한식집")
                .status(OPEN)
                .rating(rr("4.9", 5))
                .categories(new HashSet<>(List.of(korean)))
                .build();

        // 필터링 데이터
        Restaurant r4Shutdown = Restaurant.builder()
                .restaurantId("R4")
                .name("폐업가게")
                .phone("02-0000-0004")
                .address("종로")
                .longitude(126.9830)
                .latitude(37.5705)
                .description("폐업")
                .status(OPEN)
                .rating(rr("3.7", 50))
                .categories(new HashSet<>(List.of(japanese)))
                .build();
        r4Shutdown.softDelete();

        restaurantRepository.saveAll(List.of(r1, r2, r3, r4Shutdown));
        em.flush();
        em.clear();
    }

    @AfterEach
    void disableFilter() {
        em.unwrap(Session.class).disableFilter("activeRestaurantFilter");
    }

    @Test
    @DisplayName("엔티티 필터: status=SHUTDOWN 은 조회되지 않는다")
    void filter_status_shutdown() {
        var page = restaurantRepository.searchByRating(null, null, PageRequest.of(0, 20));
        var ids = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        assertThat(ids).doesNotContain("R4");
        log.info("필터 결과(삭제 제외): {}", ids);
    }

    @Test
    @DisplayName("검색어 미포함, 카테고리 미포함, 거리 정렬(ASC)")
    void distance_asc_without_keyword() {
        // given
        var pageable = distanceAsc();

        // when
        var page = restaurantRepository.searchOrderByDistance(
                37.5665, 126.9780, null, null, pageable
        );
        var ids = page.getContent().stream().map(RestaurantListResponseDto::getRestaurantId).toList();

        // then
        assertThat(ids).containsExactly("R1", "R2", "R3");

        log.info("거리순 ids: {}", ids);
    }

    @Test
    @DisplayName("검색어 미포함, 카테고리 미포함, 별점 정렬(DESC)")
    void rating_desc_without_keyword() {
        // given
        PageRequest pageable = ratingDesc();

        // when
        var page = restaurantRepository.searchByRating(null, null, pageable);
        var ids = page.getContent().stream().map(RestaurantListResponseDto::getRestaurantId).toList();

        // then
        assertThat(ids).containsExactly("R3", "R1", "R2");

        log.info("별점 내림차순 ids: {}", ids);
    }

    @Test
    @DisplayName("검색어 미포함, 카테고리 미포함, 별점 정렬(ASC)")
    void rating_asc_without_keyword() {
        // given
        var pageable = ratingAsc();

        // when
        var page = restaurantRepository.searchByRating(null, null, pageable);
        var ids = page.getContent().stream().map(RestaurantListResponseDto::getRestaurantId).toList();

        // then
        assertThat(ids).containsExactly("R2", "R1", "R3");

        log.info("별점 오름차순 ids: {}", ids);
    }

    @Test
    @DisplayName("검색어 포함, 카테고리 미포함, 거리 정렬(ASC)")
    void distance_with_keyword() {
        // given
        var pageable = distanceAsc();

        // when
        var page = restaurantRepository.searchOrderByDistance(
                37.5665, 126.9780, "한식", null, pageable
        );
        var ids = page.getContent().stream().map(RestaurantListResponseDto::getRestaurantId).toList();

        // then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(ids).containsExactly("R1", "R3");

        log.info("거리순 + 키워드('한식') ids: {}", ids);
    }

    @Test
    @DisplayName("검색어 포함, 카테고리 미포함, 별점 정렬(DESC)")
    void rating_desc_with_keyword() {
        // given
        var pageable = ratingDesc();

        // when
        var page = restaurantRepository.searchByRating("한식", null, pageable);
        var ids = page.getContent().stream().map(RestaurantListResponseDto::getRestaurantId).toList();

        // then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(ids).containsExactly("R3", "R1");

        log.info("별점 내림차순 + 키워드('한식') ids: {}", ids);
    }

    @Test
    @DisplayName("검색어 포함, 카테고리 미포함, 별점 정렬(ASC)")
    void rating_asc_with_keyword() {
        // given
        var pageable = ratingAsc();

        // when
        var page = restaurantRepository.searchByRating("한식", null, pageable);
        var ids = page.getContent().stream().map(RestaurantListResponseDto::getRestaurantId).toList();

        // then
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(ids).containsExactly("R1", "R3");

        log.info("별점 내림차순 + 키워드('한식') ids: {}", ids);
    }

    PageRequest distanceAsc() {
        return PageRequest.of(0, 10, Sort.by(Sort.Order.asc("distance")));
    }

    PageRequest ratingDesc() {
        return PageRequest.of(0, 10, Sort.by(Sort.Order.desc("rating")));
    }

    PageRequest ratingAsc() {
        return PageRequest.of(0, 10, Sort.by(Sort.Order.asc("rating")));
    }

}