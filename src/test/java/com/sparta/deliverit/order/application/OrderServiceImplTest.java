package com.sparta.deliverit.order.application;

import com.sparta.deliverit.order.domain.entity.OrderItem;
import com.sparta.deliverit.order.domain.entity.OrderStatus;
import com.sparta.deliverit.order.exception.NotFoundOrderException;
import com.sparta.deliverit.order.exception.NotFoundOrderItemException;
import com.sparta.deliverit.order.infrastructure.OrderItemRepository;
import com.sparta.deliverit.order.infrastructure.OrderRepository;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForOwner;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForUser;
import com.sparta.deliverit.order.presentation.dto.response.OrderInfo;
import com.sparta.deliverit.payment.application.service.PaymentService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    OrderItemRepository orderItemRepository;

    @Mock
    PaymentService paymentService;

    @InjectMocks
    OrderServiceImpl orderServiceImpl;

    @DisplayName("주문 단 건 조회시, 조회 대상이 존재하지 않는 경우 NotFoundOrderException이 발생한다.")
    @Test
    void getOrderDetailForUserWithNotFoundOrderExceptionTest() {
        // given
        Mockito.when(orderRepository.getByOrderIdForUser("1f3a3b0e-7a4c-45a0-b3c2-6e9f35dbf8a2"))
                .thenReturn(Optional.empty());

        // when // then
        Assertions.assertThatThrownBy(() -> orderServiceImpl.getOrderDetailForUser("1f3a3b0e-7a4c-45a0-b3c2-6e9f35dbf8a2", "4e26b6fc-0f41-4f7b-9c3b-9fbb7a7df943"))
                .isInstanceOf(NotFoundOrderException.class);
    }

    @DisplayName("주문을 조회하는 사용자와 주문의 사용자가 일치하지 않는 경우 AccessDeniedException이 발생한다.")
    @Test
    void getOrderDetailForUserWithAccessDeniedExceptionTest() {
        // given
        OrderDetailForUser stubDetail = getStubDetail(
                "2",
                "tester1",
                "11111111-1111-1111-1111-111111111111",
                "맛있는집",
                "00000000-0000-0000-0000-000000000002",
                LocalDateTime.of(2025, 10, 8, 11, 0, 0),
                OrderStatus.CREATED,
                "서울시 중구 어딘가 1-1",
                new BigDecimal(35000),
                0L
        );

        Mockito.when(orderRepository.getByOrderIdForUser("00000000-0000-0000-0000-000000000002"))
                .thenReturn(Optional.of(stubDetail));

        // when // then
        Assertions.assertThatThrownBy(() -> orderServiceImpl.getOrderDetailForUser("00000000-0000-0000-0000-000000000002", "1"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @DisplayName("주문을 조회하는 과정에서 주문의 주문 아이템 목록이 비어있다면, NotFoundOrderItemException이 발생한다.")
    @Test
    void getOrderDetailForUserWithNotFoundOrderItemException() {
        // given
        OrderDetailForUser stubDetail = getStubDetail("2",
                "tester1",
                "11111111-1111-1111-1111-111111111111",
                "맛있는집",
                "00000000-0000-0000-0000-000000000002",
                LocalDateTime.of(2025, 10, 8, 11, 0, 0),
                OrderStatus.CREATED,
                "서울시 중구 어딘가 1-1",
                new BigDecimal(35000),
                0L
        );

        Mockito.when(orderRepository.getByOrderIdForUser("00000000-0000-0000-0000-000000000002"))
                .thenReturn(Optional.of(stubDetail));

        Mockito.when(orderItemRepository.findAllByOrder("00000000-0000-0000-0000-000000000002"))
                .thenReturn(List.of());

        // when // then
        Assertions.assertThatThrownBy(() -> orderServiceImpl.getOrderDetailForUser("00000000-0000-0000-0000-000000000002", "2"))
                .isInstanceOf(NotFoundOrderItemException.class);
    }

    @DisplayName("주문을 조회하면 Order, OrderItem 엔티티를 OrderInfo, MenuInfo DTO로 변환하고 OrderDto를 반환한다.")
    @Test
    void getOrderDetailForUserTest() {
        // given
        OrderDetailForUser stubDetail = getStubDetail("2",
                "tester1",
                "11111111-1111-1111-1111-111111111111",
                "맛있는집",
                "00000000-0000-0000-0000-000000000002",
                LocalDateTime.of(2025, 10, 8, 11, 0, 0),
                OrderStatus.CREATED,
                "서울시 중구 어딘가 1-1",
                new BigDecimal(35000),
                0L
        );

        Mockito.when(orderRepository.getByOrderIdForUser("00000000-0000-0000-0000-000000000002"))
                .thenReturn(Optional.of(stubDetail));

        Mockito.when(orderItemRepository.findAllByOrder("00000000-0000-0000-0000-000000000002"))
                .thenReturn(List.of(
                        OrderItem.create(
                                "10000000-0000-0000-0000-000000000001",
                                null,
                                null,
                                "짜장면",
                                new BigDecimal(7000),
                                2
                        ),
                        OrderItem.create(
                                "10000000-0000-0000-0000-000000000002",
                                null,
                                null,
                                "탕수육",
                                new BigDecimal(21000),
                                1
                        )
                ));

        // when
        OrderInfo orderInfo = orderServiceImpl.getOrderDetailForUser("00000000-0000-0000-0000-000000000002", "2");

        // then
        Assertions.assertThat(orderInfo.getOrderId()).isEqualTo("00000000-0000-0000-0000-000000000002");
        Assertions.assertThat(orderInfo.getRestaurantName()).isEqualTo("맛있는집");
        Assertions.assertThat(orderInfo.getUsername()).isEqualTo("tester1");
        Assertions.assertThat(orderInfo.getOrderTime()).isEqualTo(LocalDateTime.of(2025, 10, 8, 11, 0, 0).toString());
        Assertions.assertThat(orderInfo.getOrderStatus()).isEqualTo("주문 생성");
        Assertions.assertThat(orderInfo.getDeliveryAddress()).isEqualTo("서울시 중구 어딘가 1-1");
        Assertions.assertThat(orderInfo.getMenus()).size().isEqualTo(2);
        Assertions.assertThat(orderInfo.getMenus().get(1).getMenuName()).isEqualTo("탕수육");
        Assertions.assertThat(orderInfo.getMenus().get(1).getPrice()).isEqualByComparingTo("21000");
        Assertions.assertThat(orderInfo.getMenus().get(1).getQuantity()).isEqualTo(1);
    }

    @DisplayName("음식점에서 주문 단 건 조회시, 조회 대상이 존재하지 않는 경우 NotFoundOrderException이 발생한다.")
    @Test
    void getOrderDetailForOwnerWithNotFoundOrderExceptionTest() {
        // given
        Mockito.when(orderRepository.getByOrderIdForOwner("1f3a3b0e-7a4c-45a0-b3c2-6e9f35dbf8a2"))
                .thenReturn(Optional.empty());

        // when // then
        Assertions.assertThatThrownBy(() -> orderServiceImpl.getOrderDetailForOwner("1f3a3b0e-7a4c-45a0-b3c2-6e9f35dbf8a2", "4e26b6fc-0f41-4f7b-9c3b-9fbb7a7df943"))
                .isInstanceOf(NotFoundOrderException.class);
    }

    @DisplayName("음식점에서 조회하는 주문이 본인 음식점의 주문이 아닌 경우 AccessDeniedException이 발생한다.")
    @Test
    void getOrderDetailForOwnerWithAccessDeniedExceptionTest() {
        // given
        OrderDetailForOwner stubDetail = getStubDetailForOwner(
                "1",
                "tester1",
                "11111111-1111-1111-1111-111111111111",
                "2",
                "맛있는집",
                "00000000-0000-0000-0000-000000000002",
                LocalDateTime.of(2025, 10, 8, 11, 0, 0),
                OrderStatus.CREATED,
                "서울시 중구 어딘가 1-1",
                new BigDecimal(35000),
                0L
        );

        Mockito.when(orderRepository.getByOrderIdForOwner("00000000-0000-0000-0000-000000000002"))
                .thenReturn(Optional.of(stubDetail));

        // when // then
        Assertions.assertThatThrownBy(() -> orderServiceImpl.getOrderDetailForOwner("00000000-0000-0000-0000-000000000002", "1"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @DisplayName("음식점에서 주문을 조회하는 과정에서 주문의 주문 아이템 목록이 비어있다면, NotFoundOrderItemException이 발생한다.")
    @Test
    void getOrderDetailForOwnerWithNotFoundOrderItemException() {
        // given
        OrderDetailForOwner stubDetail = getStubDetailForOwner(
                "2",
                "tester1",
                "11111111-1111-1111-1111-111111111111",
                "2",
                "맛있는집",
                "00000000-0000-0000-0000-000000000002",
                LocalDateTime.of(2025, 10, 8, 11, 0, 0),
                OrderStatus.CREATED,
                "서울시 중구 어딘가 1-1",
                new BigDecimal(35000),
                0L
        );

        Mockito.when(orderRepository.getByOrderIdForOwner("00000000-0000-0000-0000-000000000002"))
                .thenReturn(Optional.of(stubDetail));

        Mockito.when(orderItemRepository.findAllByOrder("00000000-0000-0000-0000-000000000002"))
                .thenReturn(List.of());

        // when // then
        Assertions.assertThatThrownBy(() -> orderServiceImpl.getOrderDetailForOwner("00000000-0000-0000-0000-000000000002", "2"))
                .isInstanceOf(NotFoundOrderItemException.class);
    }

    @DisplayName("음식점에서 주문을 조회하면 OrderDetailForOwner, OrderItem 엔티티를 OrderInfo, MenuInfo로 변환하고 OrderInfo를 반환한다.")
    @Test
    void getOrderDetailForOwnerTest() {
        // given
        OrderDetailForOwner stubDetail = getStubDetailForOwner(
                "2",
                "tester1",
                "11111111-1111-1111-1111-111111111111",
                "2",
                "맛있는집",
                "00000000-0000-0000-0000-000000000002",
                LocalDateTime.of(2025, 10, 8, 11, 0, 0),
                OrderStatus.CREATED,
                "서울시 중구 어딘가 1-1",
                new BigDecimal(35000),
                0L
        );

        Mockito.when(orderRepository.getByOrderIdForOwner("00000000-0000-0000-0000-000000000002"))
                .thenReturn(Optional.of(stubDetail));

        Mockito.when(orderItemRepository.findAllByOrder("00000000-0000-0000-0000-000000000002"))
                .thenReturn(List.of(
                        OrderItem.create(
                                "10000000-0000-0000-0000-000000000001",
                                null,
                                null,
                                "짜장면",
                                new BigDecimal(7000),
                                2
                        ),
                        OrderItem.create(
                                "10000000-0000-0000-0000-000000000002",
                                null,
                                null,
                                "탕수육",
                                new BigDecimal(21000),
                                1
                        )
                ));

        // when
        OrderInfo orderInfo = orderServiceImpl.getOrderDetailForOwner("00000000-0000-0000-0000-000000000002", "2");

        // then
        Assertions.assertThat(orderInfo.getOrderId()).isEqualTo("00000000-0000-0000-0000-000000000002");
        Assertions.assertThat(orderInfo.getRestaurantName()).isEqualTo("맛있는집");
        Assertions.assertThat(orderInfo.getUsername()).isEqualTo("tester1");
        Assertions.assertThat(orderInfo.getOrderTime()).isEqualTo(LocalDateTime.of(2025, 10, 8, 11, 0, 0).toString());
        Assertions.assertThat(orderInfo.getOrderStatus()).isEqualTo("주문 생성");
        Assertions.assertThat(orderInfo.getDeliveryAddress()).isEqualTo("서울시 중구 어딘가 1-1");
        Assertions.assertThat(orderInfo.getMenus()).size().isEqualTo(2);
        Assertions.assertThat(orderInfo.getMenus().get(1).getMenuName()).isEqualTo("탕수육");
        Assertions.assertThat(orderInfo.getMenus().get(1).getPrice()).isEqualByComparingTo("21000");
        Assertions.assertThat(orderInfo.getMenus().get(1).getQuantity()).isEqualTo(1);
    }

    private static OrderDetailForUser getStubDetail(
            String userId,
            String username,
            String restaurantId,
            String restaurantName,
            String orderId,
            LocalDateTime orderedAt,
            OrderStatus orderStatus,
            String address,
            BigDecimal totalPrice,
            Long version) {
        return new OrderDetailForUser() {
            @Override
            public String getUserId() {
                return userId;
            }

            @Override
            public String getUserName() {
                return username;
            }

            @Override
            public String getRestaurantId() {
                return restaurantId;
            }

            @Override
            public String getRestaurantName() {
                return restaurantName;
            }

            @Override
            public String getOrderId() {
                return orderId;
            }

            @Override
            public LocalDateTime getOrderedAt() {
                return orderedAt;
            }

            @Override
            public OrderStatus getOrderStatus() {
                return orderStatus;
            }

            @Override
            public String getAddress() {
                return address;
            }

            @Override
            public BigDecimal getTotalPrice() {
                return totalPrice;
            }

            @Override
            public Long getVersion() {
                return version;
            }
        };
    }

    private static OrderDetailForOwner getStubDetailForOwner(
            String userId,
            String username,
            String restaurantId,
            String restaurantUserId,
            String restaurantName,
            String orderId,
            LocalDateTime orderAt,
            OrderStatus orderStatus,
            String address,
            BigDecimal totalPrice,
            Long version) {
        return new OrderDetailForOwner() {
            @Override
            public String getUserId() {
                return userId;
            }

            @Override
            public String getUserName() {
                return username;
            }

            @Override
            public String getRestaurantId() {
                return restaurantId;
            }

            @Override
            public String getRestaurantUserId() {
                return restaurantUserId;
            }

            @Override
            public String getRestaurantName() {
                return restaurantName;
            }

            @Override
            public String getOrderId() {
                return orderId;
            }

            @Override
            public LocalDateTime getOrderedAt() {
                return orderAt;
            }

            @Override
            public OrderStatus getOrderStatus() {
                return orderStatus;
            }

            @Override
            public String getAddress() {
                return address;
            }

            @Override
            public BigDecimal getTotalPrice() {
                return totalPrice;
            }

            @Override
            public Long getVersion() {
                return version;
            }
        };
    }

}