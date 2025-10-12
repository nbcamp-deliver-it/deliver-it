package com.sparta.deliverit.order.infrastructure;

import com.sparta.deliverit.order.domain.entity.OrderStatus;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForOwner;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
@Sql({"/sql/user_restaurant_insert.sql", "/sql/menu_insert.sql", "/sql/order_insert.sql", "/sql/orderitem_insert.sql"})
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @DisplayName("고객이 주문 단건 조회시, OrderDetailForUser 프로젝션으로 값을 가져온다.")
    @Test
    void getByOrderIdForUserTest() {
        // when
        Optional<OrderDetailForUser> findOrder = orderRepository.getByOrderIdForUser("00000000-0000-0000-0000-000000000001");

        // then
        Assertions.assertThat(findOrder).isPresent();
        Assertions.assertThat(findOrder).hasValueSatisfying(order -> {
            Assertions.assertThat(order.getOrderId()).isEqualTo("00000000-0000-0000-0000-000000000001");
            Assertions.assertThat(order.getUserId()).isEqualTo("1");
            Assertions.assertThat(order.getUserName()).isEqualTo("tester1");
            Assertions.assertThat(order.getRestaurantId()).isEqualTo("11111111-1111-1111-1111-111111111111");
            Assertions.assertThat(order.getRestaurantName()).isEqualTo("맛있는집");
            Assertions.assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CREATED);
            Assertions.assertThat(order.getAddress()).isEqualTo("서울시 중구 어딘가 1-1");
            Assertions.assertThat(order.getTotalPrice()).isEqualByComparingTo("40000");
            Assertions.assertThat(order.getVersion()).isEqualByComparingTo(0L);
        });
    }

    @DisplayName("음식점에서 주문 단건 조회시, OrderDetailForOwner 프로젝션으로 값을 가져온다.")
    @Test
    void getByOrderIdForUserForOwnerTest() {
        // when
        Optional<OrderDetailForOwner> findOrder = orderRepository.getByOrderIdForOwner("00000000-0000-0000-0000-000000000001");

        // then
        Assertions.assertThat(findOrder).isPresent();
        Assertions.assertThat(findOrder).hasValueSatisfying(order -> {
            Assertions.assertThat(order.getOrderId()).isEqualTo("00000000-0000-0000-0000-000000000001");
            Assertions.assertThat(order.getUserName()).isEqualTo("tester1");
            Assertions.assertThat(order.getRestaurantId()).isEqualTo("11111111-1111-1111-1111-111111111111");
            Assertions.assertThat(order.getRestaurantUserId()).isEqualTo("2");
            Assertions.assertThat(order.getRestaurantName()).isEqualTo("맛있는집");
            Assertions.assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CREATED);
            Assertions.assertThat(order.getAddress()).isEqualTo("서울시 중구 어딘가 1-1");
            Assertions.assertThat(order.getTotalPrice()).isEqualByComparingTo("40000");
            Assertions.assertThat(order.getVersion()).isEqualByComparingTo(0L);
        });
    }
}