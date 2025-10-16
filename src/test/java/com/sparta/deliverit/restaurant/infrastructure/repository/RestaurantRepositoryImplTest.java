package com.sparta.deliverit.restaurant.infrastructure.repository;

import com.sparta.deliverit.anything.config.QuerydslConfig;
import com.sparta.deliverit.restaurant.domain.model.PageSize;
import com.sparta.deliverit.restaurant.domain.model.RestaurantCategory;
import com.sparta.deliverit.restaurant.presentation.dto.RestaurantListResponseDto;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static com.sparta.deliverit.restaurant.domain.model.RestaurantCategory.KOREAN_FOOD;
import static com.sparta.deliverit.restaurant.domain.model.SortType.CREATED_AT;
import static com.sparta.deliverit.restaurant.domain.model.SortType.RATING;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
class RestaurantRepositorySliceTest {

    @Autowired
    private RestaurantRepository rr;

    @Autowired
    private EntityManager em;

    private static final int PAGE_NUMBER = 0;
    private static final int REQUEST_PAGE_SIZE = 15;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final double SEOUL_CITY_HALL_LAT = 37.5665;
    private static final double SEOUL_CITY_HALL_LON = 126.9780;

    // 하이버네이트 엔티티 필터 활성화
    @BeforeEach
    void enableFilter() {
        em.unwrap(Session.class)
                .enableFilter("activeRestaurantFilter");
    }

    // 하이버네이트 엔티티 필터 비활성화
    @AfterEach
    void disableFilter() {
        em.unwrap(Session.class)
                .disableFilter("activeRestaurantFilter");
    }

    @Test
    @DisplayName("엔티티 필터: status=SHUTDOWN은 조회되지 않는다 + 페이징: 10, 30, 50 외의 size가 들어오면 10으로 변환된다.")
    void filter_status_shutdown() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page =
                rr.searchByCreatedAt(null, null, pageable);

        var list = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        // then
        assertThatPage(page);

        assertThat(list.size()).isEqualTo(9);
        assertThat(list).doesNotContain("RES-005");
    }

    @Test
    @DisplayName("searchByCreatedAt: 생성일 내림차순 정렬")
    void searchByCreatedAt_createdAt_desc_and_paging() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page =
                rr.searchByCreatedAt(null, null, pageable);

        var list = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        // then
        assertThatPage(page);

        assertThat(list.get(0)).isEqualTo("RES-010");
        assertThat(list).containsExactly(
                "RES-010", "RES-009", "RES-008", "RES-007",
                "RES-006", "RES-004", "RES-003", "RES-002", "RES-001"
        );
    }

    @Test
    @DisplayName("searchByCreatedAt: 생성일 오름차순 정렬")
    void searchByCreatedAt_createdAt_asc_and_paging() {
        // given
        var pageable = getPageableCreatedAtAsc(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page =
                rr.searchByCreatedAt(null, null, pageable);

        var list = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        // then
        assertThatPage(page);

        assertThat(list.get(0)).isEqualTo("RES-001");
        assertThat(list).containsExactly(
                "RES-001", "RES-002", "RES-003", "RES-004",
                "RES-006", "RES-007", "RES-008", "RES-009", "RES-010"
        );
    }

    @Test
    @DisplayName("searchByCreatedAt: 이름에 '집' 키워드가 포함된 결과를 최신순으로 정렬")
    void searchByCreatedAt_createdAt_with_keyword() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        String keyword = "집";

        // when
        var page =
                rr.searchByCreatedAt(keyword, null, pageable);

        var list = page.getContent();

        // then
        assertThatPage(page);

        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0).getRestaurantId()).isEqualTo("RES-009");
        assertThat(list).
                extracting(RestaurantListResponseDto::getRestaurantId)
                .containsExactly("RES-009", "RES-008", "RES-007");
        assertThat(list.get(0).getName()).contains("집");
    }

    @Test
    @DisplayName("searchByCreatedAt: 카테고리가 한식인 결과를 최신순으로 정렬")
    void searchByCreatedAt_createdAt_with_category() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        RestaurantCategory category = KOREAN_FOOD;

        // when
        var page =
                rr.searchByCreatedAt(null, category, pageable);

        var list = page.getContent();

        // then
        assertThatPage(page);

        assertThat(list.size()).isEqualTo(3);
        assertThat(list.get(0).getRestaurantId()).isEqualTo("RES-009");
        assertThat(list).
                extracting(RestaurantListResponseDto::getRestaurantId)
                .containsExactly("RES-009", "RES-007", "RES-001");
    }

    @Test
    @DisplayName("searchByCreatedAt: 카테고리가 한식이면서 이름에 '집' 키워드가 들어가는 결과를 최신순으로 정렬")
    void searchByCreatedAt_createdAt_with_search_and_category() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        String keyword = "집";
        RestaurantCategory category = KOREAN_FOOD;

        // when
        var page =
                rr.searchByCreatedAt(keyword, category, pageable);

        var list = page.getContent();

        // then
        assertThatPage(page);

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0).getRestaurantId()).isEqualTo("RES-009");
        assertThat(list).
                extracting(RestaurantListResponseDto::getRestaurantId)
                .containsExactly("RES-009", "RES-007");
    }

    @Test
    @DisplayName("searchOrderByDistance: 서울시청 기준 가까운순으로 정렬")
    void searchOrderByDistance_distance_asc_and_paging() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page =
                rr.searchOrderByDistance(SEOUL_CITY_HALL_LAT, SEOUL_CITY_HALL_LON, null, null, pageable);

        var list = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        // then
        assertThatPage(page);

        assertThat(list.get(0)).isEqualTo("RES-002");
        assertThat(list)
                .containsExactly(
                        "RES-002", "RES-003", "RES-004", "RES-001",
                        "RES-010", "RES-007", "RES-008", "RES-006", "RES-009"
                );
    }

    @Test
    @DisplayName("searchOrderByDistance: 이름에 '집' 키워드가 포함된 결과 중 서울 시청 기준 가까운 순서으로 정렬")
    void searchOrderByDistance_distance_with_keyword() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        String keyword = "집";

        // when
        var page =
                rr.searchOrderByDistance(SEOUL_CITY_HALL_LAT, SEOUL_CITY_HALL_LON, keyword, null, pageable);

        var list = page.getContent();

        // then
        assertThatPage(page);

        assertThat(list.size()).isEqualTo(3);
        assertThat(list)
                .extracting(RestaurantListResponseDto::getRestaurantId)
                .containsExactly("RES-007", "RES-008", "RES-009");
    }

    @Test
    @DisplayName("searchByRating: 별점 내림차순으로 정렬")
    void searchByRating_rating_desc_and_paging() {
        // given
        var pageable = getPageableDefault(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page =
                rr.searchByRating(null, null, pageable);

        var list = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        // then
        assertThatPage(page);

        assertThat(list.get(0)).isEqualTo("RES-003");
        assertThat(list)
                .containsExactly(
                        "RES-003", "RES-009", "RES-006", "RES-001",
                        "RES-010", "RES-007", "RES-002", "RES-008", "RES-004"
                );

    }

    @Test
    @DisplayName("searchByRating: 별점 오름차순으로 정렬")
    void searchByRating_rating_asc_and_paging() {
        // given
        var pageable = getPageableRatingAsc(PAGE_NUMBER, PageSize.normalize(REQUEST_PAGE_SIZE));

        // when
        var page =
                rr.searchByRating(null, null, pageable);

        var list = page.map(RestaurantListResponseDto::getRestaurantId).getContent();

        // then
        assertThatPage(page);

        assertThat(list.get(0)).isEqualTo("RES-004");
        assertThat(list)
                .containsExactly(
                        "RES-004", "RES-008", "RES-002", "RES-007",
                        "RES-010", "RES-001", "RES-006", "RES-009", "RES-003"
                );
    }

    Pageable getPageableDefault(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize);
    }

    Pageable getPageableCreatedAtAsc(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc(CREATED_AT.field())));
    }

    Pageable getPageableRatingAsc(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc(RATING.field())));
    }

    void assertThatPage(Page<RestaurantListResponseDto> page) {
        assertThat(page).isNotNull();
        assertThat(page.getSize()).isEqualTo(DEFAULT_PAGE_SIZE);
        assertThat(page.getNumber()).isZero();
    }
}
