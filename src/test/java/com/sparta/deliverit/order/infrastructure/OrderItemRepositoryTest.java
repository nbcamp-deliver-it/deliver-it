//package com.sparta.deliverit.order.infrastructure;
//
//import com.sparta.deliverit.order.domain.entity.OrderItem;
//import jakarta.persistence.EntityManager;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//import java.util.Set;
//
//@DataJpaTest
//@ActiveProfiles("test")
//class OrderItemRepositoryTest {
//
//    @Autowired
//    OrderItemRepository orderItemRepository;
//
//    @Autowired
//    EntityManager em;
//
//    @BeforeEach
//    void seed() {
//        // users
//        em.createNativeQuery("""
//      INSERT INTO users (id, username, password, name, phone, role, created_at)
//      VALUES (1, 'tester1', '{noop}pw1234', '홍길동', '010-1000-1000', 'CUSTOMER', CURRENT_TIMESTAMP)
//    """).executeUpdate();
//        em.createNativeQuery("""
//      INSERT INTO users (id, username, password, name, phone, role, created_at)
//      VALUES (2, 'tester2', '{noop}pw4321', '이순신', '010-1234-5678', 'OWNER', CURRENT_TIMESTAMP)
//    """).executeUpdate();
//        // IDENTITY 재시작
//        em.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 3").executeUpdate();
//
//        // restaurant
//        em.createNativeQuery("""
//      INSERT INTO p_restaurant (restaurant_id, name, user_id, phone, address, longitude, latitude, description, status, deleted)
//      VALUES ('11111111-1111-1111-1111-111111111111','맛있는집',2,'02-123-4567','서울시 중구 어딘가 1-1',126.9780,37.5665,'김치찌개 전문','OPEN',FALSE)
//    """).executeUpdate();
//
//        // menus
//        em.createNativeQuery("""
//      INSERT INTO p_menu (menu_id, restaurant_id, name, price, status, description) VALUES
//      ('3dfebfd6-c60c-4dcf-ab76-1946b3641dc7','11111111-1111-1111-1111-111111111111','짜장면',7000,'SELLING','맛있는 짜장면'),
//      ('3f2faf2f-ec74-4e78-b3ea-1acbaf1cbd29','11111111-1111-1111-1111-111111111111','탕수육',21000,'SELLING','맛있는 탕수육')
//    """).executeUpdate();
//
//        // orders
//        em.createNativeQuery("""
//      INSERT INTO p_order (order_id, user_id, restaurant_id, ordered_at, created_at, updated_at, order_status, address, total_price, version) VALUES
//      ('00000000-0000-0000-0000-000000000001', 1, '11111111-1111-1111-1111-111111111111', TIMESTAMP '2025-10-01 12:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ORDER_CREATED', '서울시 중구 어딘가 1-1', 40000, 0),
//      ('00000000-0000-0000-0000-000000000002', 1, '11111111-1111-1111-1111-111111111111', TIMESTAMP '2025-10-08 12:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ORDER_CREATED', '서울시 중구 어딘가 1-1', 70000, 0),
//      ('00000000-0000-0000-0000-000000000003', 1, '11111111-1111-1111-1111-111111111111', TIMESTAMP '2025-10-10 12:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ORDER_COMPLETED','서울시 중구 어딘가 1-1', 70000, 0),
//      ('00000000-0000-0000-0000-000000000004', 1, '11111111-1111-1111-1111-111111111111', TIMESTAMP '2025-10-10 12:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ORDER_COMPLETED','서울시 중구 어딘가 1-1', 70000, 0),
//      ('00000000-0000-0000-0000-000000000005', 1, '11111111-1111-1111-1111-111111111111', TIMESTAMP '2025-10-10 12:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'ORDER_CONFIRMED','서울시 중구 어딘가 1-1', 70000, 0)
//    """).executeUpdate();
//
//        // order items
//        em.createNativeQuery("""
//      INSERT INTO p_order_item (order_item_id, order_id, menu_id, menu_name_snapshot, menu_price_snapshot, quantity, created_at, updated_at) VALUES
//      ('045353eb-75cd-48ce-b225-1f9d56e8c90d','00000000-0000-0000-0000-000000000001','3dfebfd6-c60c-4dcf-ab76-1946b3641dc7','짜장면',6000,2,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
//      ('e162358d-2375-4807-9863-df97f9b63516','00000000-0000-0000-0000-000000000001','3f2faf2f-ec74-4e78-b3ea-1acbaf1cbd29','탕수육',18000,1,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
//      ('045353eb-75cd-48ce-b225-1f9d56e8c900','00000000-0000-0000-0000-000000000002','3dfebfd6-c60c-4dcf-ab76-1946b3641dc7','착한 짜장면',7000,4,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
//      ('e162358d-2375-4807-9863-df97f9b63510','00000000-0000-0000-0000-000000000002','3f2faf2f-ec74-4e78-b3ea-1acbaf1cbd29','착한 탕수육',21000,2,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
//      ('045353eb-75cd-48ce-b225-1f9d56e8c905','00000000-0000-0000-0000-000000000003','3dfebfd6-c60c-4dcf-ab76-1946b3641dc7','착한 짜장면',7000,4,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
//      ('e162358d-2375-4807-9863-df97f9b63512','00000000-0000-0000-0000-000000000003','3f2faf2f-ec74-4e78-b3ea-1acbaf1cbd29','착한 탕수육',21000,2,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
//      ('045353eb-75cd-48ce-b225-1f9d56e8c90a','00000000-0000-0000-0000-000000000004','3dfebfd6-c60c-4dcf-ab76-1946b3641dc7','착한 짜장면',7000,4,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
//      ('e162358d-2375-4807-9863-df97f9b6351f','00000000-0000-0000-0000-000000000004','3f2faf2f-ec74-4e78-b3ea-1acbaf1cbd29','착한 탕수육',21000,2,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
//      ('045353eb-75cd-41ce-b225-1f9d56e8c90a','00000000-0000-0000-0000-000000000005','3dfebfd6-c60c-4dcf-ab76-1946b3641dc7','착한 짜장면',7000,4,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
//      ('e162358d-2375-4107-9863-df97f9b6351f','00000000-0000-0000-0000-000000000005','3f2faf2f-ec74-4e78-b3ea-1acbaf1cbd29','착한 탕수육',21000,2,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)
//    """).executeUpdate();
//    }
//
//    @DisplayName("주문과 관련된 주문아이템 목록을 가져온다.")
//    @Test
//    void findAllByOrderInTest() {
//        // when
//        List<OrderItem> orderItemList = orderItemRepository.findAllByOrderIn(Set.of("00000000-0000-0000-0000-000000000001", "00000000-0000-0000-0000-000000000002"));
//
//        // then
//        Assertions.assertThat(orderItemList).size().isEqualTo(4);
//        Assertions.assertThat(orderItemList.get(0).getMenu().getId()).isEqualTo("3dfebfd6-c60c-4dcf-ab76-1946b3641dc7");
//        Assertions.assertThat(orderItemList.get(0).getMenuNameSnapshot()).isEqualTo("짜장면");
//        Assertions.assertThat(orderItemList.get(0).getMenuPriceSnapshot()).isEqualByComparingTo("6000");
//        Assertions.assertThat(orderItemList.get(0).getQuantity()).isEqualTo(2);
//        Assertions.assertThat(orderItemList.get(3).getMenu().getId()).isEqualTo("3f2faf2f-ec74-4e78-b3ea-1acbaf1cbd29");
//        Assertions.assertThat(orderItemList.get(3).getMenuNameSnapshot()).isEqualTo("착한 탕수육");
//        Assertions.assertThat(orderItemList.get(3).getMenuPriceSnapshot()).isEqualByComparingTo("21000");
//        Assertions.assertThat(orderItemList.get(3).getQuantity()).isEqualTo(2);
//    }
//}