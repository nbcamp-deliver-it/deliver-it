package com.sparta.deliverit.order.infrastructure;

import com.sparta.deliverit.order.domain.entity.OrderStatus;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForOwner;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
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

    @DisplayName("주문 목록 조회시, from 시간대부터 to 사이의 주문 목록을 가져온다. (from <= X < to) 이때, 정확히 to와 일치하는 주문과 이후 주문은 읽어오지 않는다.")
    @Test
    void findOrdersByUserIdTest() {
        // when
        Page<OrderDetailForUser> pageOrderList = orderRepository.findOrdersByUserIdForUser(
                "1",
                LocalDateTime.of(2025, 10, 1, 12, 0, 0),
                LocalDateTime.of(2025, 10, 8, 12, 0, 0),
                PageRequest.of(0, 10));

        List<OrderDetailForUser> findOrderList = pageOrderList.getContent();
        // then
        Assertions.assertThat(findOrderList).size().isEqualTo(1);
        Assertions.assertThat(findOrderList.get(0).getOrderId()).isEqualTo("00000000-0000-0000-0000-000000000001");
        Assertions.assertThat(findOrderList.get(0).getTotalPrice()).isEqualByComparingTo("40000");
    }

    @DisplayName("음식점에서 주문 목록 조회시, from 시간대부터 to 사이의 주문 목록을 가져온다. (from <= X < to) 이때, 정확히 to와 일치하는 주문과 이후 주문은 읽어오지 않는다.")
    @Test
    void findOrdersByRestaurantIdTest() {
        // when
        Page<OrderDetailForOwner> pageOrderList = orderRepository.findOrdersByRestaurantIdForOwner(
                "11111111-1111-1111-1111-111111111111",
                LocalDateTime.of(2025, 10, 1, 12, 0, 0),
                LocalDateTime.of(2025, 10, 8, 12, 0, 0),
                PageRequest.of(0, 10));

        List<OrderDetailForOwner> findOrderList = pageOrderList.getContent();
        // then
        Assertions.assertThat(findOrderList).size().isEqualTo(1);
        Assertions.assertThat(findOrderList.get(0).getOrderId()).isEqualTo("00000000-0000-0000-0000-000000000001");
        Assertions.assertThat(findOrderList.get(0).getTotalPrice()).isEqualByComparingTo("40000");
    }
}