package com.sparta.deliverit.order.infrastructure;

import com.sparta.deliverit.order.domain.entity.Order;
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

    @DisplayName("음식점에서 주문을 확인 상태로 변경하고자 하는 경우, order의 상태가 'CONFIRM'이 되고 1을 반환 그리고 version의 값이 1 증가한다.")
    @Test
    void updateOrderStatusToConfirmTest() {
        // given
        Order currentOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000003");

        Long beforeVersion = currentOrder.getVersion();

        LocalDateTime nowMinusMinute = LocalDateTime.of(2025,10,10,12,3,0).minusMinutes(5);
        // when
        int result = orderRepository.updateOrderStatusToConfirm(
                currentOrder.getOrderId(),
                currentOrder.getRestaurant().getRestaurantId(),
                2L,
                OrderStatus.PAYMENT_COMPLETED,
                OrderStatus.CONFIRMED,
                beforeVersion,
                nowMinusMinute
        );
        Order nextOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000003");

        // then
        Assertions.assertThat(result).isEqualTo(1);
        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion + 1);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @DisplayName("음식점에서 주문을 확인 상태로 변경하고자 할 때, 접근한 유저 아이디가 음식 점주가 아닌 경우 상태가 변경되지 않는다.")
    @Test
    void updateOrderStatusToConfirmTest2() {
        // given
        Order currentOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000003");

        Long beforeVersion = currentOrder.getVersion();

        LocalDateTime nowMinusMinute = LocalDateTime.of(2025,10,10,12,3,0).minusMinutes(5);
        // when
        int result = orderRepository.updateOrderStatusToConfirm(
                currentOrder.getOrderId(),
                currentOrder.getRestaurant().getRestaurantId(),
                1L,
                OrderStatus.PAYMENT_COMPLETED,
                OrderStatus.CONFIRMED,
                beforeVersion,
                nowMinusMinute
        );
        Order nextOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000003");

        // then
        Assertions.assertThat(result).isEqualTo(0);
        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    @DisplayName("음식점에서 주문을 확인 상태로 변경하고자 할 때, restaurantId가 다른 경우 상태가 변경되지 않는다.")
    @Test
    void updateOrderStatusToConfirmTest3() {
        // given
        Order currentOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000003");

        Long beforeVersion = currentOrder.getVersion();

        LocalDateTime nowMinusMinute = LocalDateTime.of(2025,10,10,12,3,0).minusMinutes(5);
        // when
        int result = orderRepository.updateOrderStatusToConfirm(
                currentOrder.getOrderId(),
                "11111111-1111-1111-1111-111111111112",
                2L,
                OrderStatus.PAYMENT_COMPLETED,
                OrderStatus.CONFIRMED,
                beforeVersion,
                nowMinusMinute
        );
        Order nextOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000003");

        // then
        Assertions.assertThat(result).isEqualTo(0);
        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    @DisplayName("음식점에서 주문을 확인 상태로 변경하고자 할 때, 주문한지 5분이 지난 경우 주문 확인은 실패한다.")
    @Test
    void updateOrderStatusToConfirmTest4() {
        // given
        Order currentOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000003");

        Long beforeVersion = currentOrder.getVersion();

        LocalDateTime nowMinusMinute = LocalDateTime.of(2025,10,10, 12,6,0).minusMinutes(5);
        // when
        int result = orderRepository.updateOrderStatusToConfirm(
                currentOrder.getOrderId(),
                currentOrder.getRestaurant().getRestaurantId(),
                2L,
                OrderStatus.PAYMENT_COMPLETED,
                OrderStatus.CONFIRMED,
                beforeVersion,
                nowMinusMinute
        );
        Order nextOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000003");

        // then
        Assertions.assertThat(result).isEqualTo(0);
        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    @DisplayName("음식점에서 주문을 확인 상태로 변경하고자 할 때, 주문한지 5분이 지나지 않은 경우 order의 상태가 'CONFIRM'이 되고 1을 반환 그리고 version의 값이 1 증가한다. ")
    @Test
    void updateOrderStatusToConfirmTest5() {
        // given
        Order currentOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000003");

        Long beforeVersion = currentOrder.getVersion();

        LocalDateTime nowMinusMinute = LocalDateTime.of(2025,10,10, 12,1,0).minusMinutes(5);
        // when
        int result = orderRepository.updateOrderStatusToConfirm(
                currentOrder.getOrderId(),
                currentOrder.getRestaurant().getRestaurantId(),
                2L,
                OrderStatus.PAYMENT_COMPLETED,
                OrderStatus.CONFIRMED,
                beforeVersion,
                nowMinusMinute
        );
        Order nextOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000003");

        // then
        Assertions.assertThat(result).isEqualTo(1);
        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion + 1);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @DisplayName("고객이 주문을 취소 상태로 변경하고자 할 때, 주문한지 5분이 지나지 않은 경우 order의 상태가 'CANCEL'이 되고 1을 반환 그리고 version의 값이 1 증가한다. ")
    @Test
    void updateOrderStatusToCancelTest() {
        // given
        Order currentOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        Long beforeVersion = currentOrder.getVersion();

        LocalDateTime nowMinusMinute = LocalDateTime.of(2025,10,10, 12,1,0).minusMinutes(5);
        // when
        int result = orderRepository.updateOrderStatusToCancelForUser(
                currentOrder.getOrderId(),
                1L,
                OrderStatus.PAYMENT_COMPLETED,
                OrderStatus.CANCELED,
                beforeVersion,
                nowMinusMinute
        );

        Order nextOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        // then
        Assertions.assertThat(result).isEqualTo(1);
        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion + 1);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @DisplayName("고객이 주문을 취소 상태로 변경하고자 할 때, 주문한지 5분이 지난 경우 updateOrderStatusToCancelForUser()는 0을 반환하고 상태가 변경되지 않아 PAYMENT_COMPLETED 상태를 유지한다..")
    @Test
    void updateOrderStatusToCancelTest2() {
        // given
        Order currentOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        Long beforeVersion = currentOrder.getVersion();

        LocalDateTime nowMinusMinute = LocalDateTime.of(2025,10,10, 12,6,0).minusMinutes(5);
        // when
        int result = orderRepository.updateOrderStatusToCancelForUser(
                currentOrder.getOrderId(),
                1L,
                OrderStatus.PAYMENT_COMPLETED,
                OrderStatus.CANCELED,
                beforeVersion,
                nowMinusMinute
        );

        Order nextOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        // then
        Assertions.assertThat(result).isEqualTo(0);
        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    @DisplayName("고객이 주문을 취소 상태로 변경하고자 할 때, 주문서 고객과 취소하고자 하는 고객이 다른 경우 updateOrderStatusToCancelForUser()는 0을 반환하고 상태가 변경되지 않아 PAYMENT_COMPLETED 상태를 유지한다..")
    @Test
    void updateOrderStatusToCancelTest3() {
        // given
        Order currentOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        Long beforeVersion = currentOrder.getVersion();

        LocalDateTime nowMinusMinute = LocalDateTime.of(2025,10,10, 12,2,0).minusMinutes(5);
        // when
        int result = orderRepository.updateOrderStatusToCancelForUser(
                currentOrder.getOrderId(),
                2L,
                OrderStatus.PAYMENT_COMPLETED,
                OrderStatus.CANCELED,
                beforeVersion,
                nowMinusMinute
        );

        Order nextOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        // then
        Assertions.assertThat(result).isEqualTo(0);
        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    @DisplayName("고객이 주문을 취소 상태로 변경하고자 할 때, 주문서 고객과 취소하고자 하는 고객이 다른 경우 updateOrderStatusToCancelForUser()는 0을 반환하고 상태가 변경되지 않아 PAYMENT_COMPLETED 상태를 유지한다..")
    @Test
    void updateOrderStatusToCancelTest4() {
        // given
        Order currentOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        Long beforeVersion = currentOrder.getVersion();

        LocalDateTime nowMinusMinute = LocalDateTime.of(2025,10,10, 12,2,0).minusMinutes(5);
        // when
        int result = orderRepository.updateOrderStatusToCancelForUser(
                currentOrder.getOrderId(),
                2L,
                OrderStatus.PAYMENT_COMPLETED,
                OrderStatus.CANCELED,
                beforeVersion,
                nowMinusMinute
        );

        Order nextOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        // then
        Assertions.assertThat(result).isEqualTo(0);
        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    @DisplayName("음식점 점주가 주문을 취소 상태로 변경하고자 할 때, 주문 상태가 PAYMENT_COMPLETED이면 'CANCEL'이 되고 1을 반환 그리고 version의 값이 1 증가한다. ")
    @Test
    void updateOrderStatusToCanceForOwnerlTest() {
        // given
        Order currentOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        Long beforeVersion = currentOrder.getVersion();

        LocalDateTime nowMinusMinute = LocalDateTime.of(2025,10,10, 12,1,0).minusMinutes(5);
        // when
        int result = orderRepository.updateOrderStatusToCancelForOwner(
                currentOrder.getOrderId(),
                "11111111-1111-1111-1111-111111111111",
                2L,
                List.of(OrderStatus.PAYMENT_COMPLETED, OrderStatus.CONFIRMED),
                OrderStatus.CANCELED,
                beforeVersion
        );

        Order nextOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        // then
        Assertions.assertThat(result).isEqualTo(1);
        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion + 1);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @DisplayName("음식점 점주가 주문을 취소 상태로 변경하고자 할 때, 주문 상태가 CONFIRMED이면 'CANCEL'이 되고 1을 반환 그리고 version의 값이 1 증가한다. ")
    @Test
    void updateOrderStatusToCanceForOwnerlTest2() {
        // given
        Order currentOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000005");

        Long beforeVersion = currentOrder.getVersion();

        LocalDateTime nowMinusMinute = LocalDateTime.of(2025,10,10, 12,1,0).minusMinutes(5);
        // when
        int result = orderRepository.updateOrderStatusToCancelForOwner(
                currentOrder.getOrderId(),
                "11111111-1111-1111-1111-111111111111",
                2L,
                List.of(OrderStatus.PAYMENT_COMPLETED, OrderStatus.CONFIRMED),
                OrderStatus.CANCELED,
                beforeVersion
        );

        Order nextOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000005");

        // then
        Assertions.assertThat(result).isEqualTo(1);
        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion + 1);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
    }



    @DisplayName("음식점 점주가 주문을 취소 상태로 변경하고자 할 때, 음식점 정보가 다른 경우 고객이 다른 경우 updateOrderStatusToCancelForUser()는 0을 반환하고 상태가 변경되지 않아 PAYMENT_COMPLETED 상태를 유지한다..")
    @Test
    void updateOrderStatusToCanceForOwnerlTest3() {
        // given
        Order currentOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        Long beforeVersion = currentOrder.getVersion();

        LocalDateTime nowMinusMinute = LocalDateTime.of(2025,10,10, 12,1,0).minusMinutes(5);
        // when
        int result = orderRepository.updateOrderStatusToCancelForOwner(
                currentOrder.getOrderId(),
                "11111111-1111-1111-1111-111111111110",
                2L,
                List.of(OrderStatus.PAYMENT_COMPLETED, OrderStatus.CONFIRMED),
                OrderStatus.CANCELED,
                beforeVersion
        );

        Order nextOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        // then
        Assertions.assertThat(result).isEqualTo(0);
        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    @DisplayName("음식점 점주가 주문을 취소 상태로 변경하고자 할 때, 음식점의 유저 정보와 요청한 유저 정보가 다른 경우 updateOrderStatusToCancelForUser()는 0을 반환하고 상태가 변경되지 않아 PAYMENT_COMPLETED 상태를 유지한다..")
    @Test
    void updateOrderStatusToCanceForOwnerlTest4() {
        // given
        Order currentOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        Long beforeVersion = currentOrder.getVersion();

        LocalDateTime nowMinusMinute = LocalDateTime.of(2025,10,10, 12,1,0).minusMinutes(5);
        // when
        int result = orderRepository.updateOrderStatusToCancelForOwner(
                currentOrder.getOrderId(),
                "11111111-1111-1111-1111-111111111111",
                1L,
                List.of(OrderStatus.PAYMENT_COMPLETED, OrderStatus.CONFIRMED),
                OrderStatus.CANCELED,
                beforeVersion
        );

        Order nextOrder = orderRepository.getReferenceById("00000000-0000-0000-0000-000000000004");

        // then
        Assertions.assertThat(result).isEqualTo(0);
        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }
}