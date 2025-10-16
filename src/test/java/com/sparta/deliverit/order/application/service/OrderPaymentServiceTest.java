package com.sparta.deliverit.order.application.service;

import com.sparta.deliverit.global.exception.PaymentException;
import com.sparta.deliverit.global.response.code.PaymentResponseCode;
import com.sparta.deliverit.order.application.dto.CreateMenuCommand;
import com.sparta.deliverit.order.application.dto.CreateOrderCommand;
import com.sparta.deliverit.order.domain.entity.Order;
import com.sparta.deliverit.order.domain.entity.OrderStatus;
import com.sparta.deliverit.order.presentation.dto.response.OrderPaymentResponse;
import com.sparta.deliverit.payment.application.service.PaymentService;

import com.sparta.deliverit.payment.domain.entity.Payment;
import com.sparta.deliverit.payment.enums.Company;
import com.sparta.deliverit.payment.enums.PayState;
import com.sparta.deliverit.payment.enums.PayType;
import com.sparta.deliverit.payment.presentation.dto.PaymentRequestDto;
import com.sparta.deliverit.restaurant.domain.entity.Restaurant;
import com.sparta.deliverit.restaurant.domain.model.RestaurantStatus;
import com.sparta.deliverit.user.domain.entity.User;
import com.sparta.deliverit.user.domain.entity.UserRoleEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.objenesis.ObjenesisStd;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderPaymentServiceTest {

    @Mock
    OrderService orderService;

    @Mock
    PaymentService paymentService;

    @Mock
    Clock clock;

    @InjectMocks
    OrderPaymentService orderPaymentService;
    
    @DisplayName("주문 결제 실행 도중 결제 쪽에서 문제가 발생하면 실패 응답을 포함한 OrderPaymentResponse를 반환한다.")
    @Test
    void checkoutTest() {
        // given
        Clock fixedClock = Clock.fixed(
                LocalDateTime.of(2025, 10, 10, 12, 3, 0)
                        .toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
        );

        Mockito.when(clock.instant()).thenReturn(fixedClock.instant());
        Mockito.when(clock.getZone()).thenReturn(fixedClock.getZone());

        CreateMenuCommand stubMenuData1 = CreateMenuCommand.builder()
                .menuId("00000000-0000-0000-0000-000000000001")
                .quantity(1)
                .build();

        CreateMenuCommand stubMenuData2 = CreateMenuCommand.builder()
                .menuId("00000000-0000-0000-0000-000000000002")
                .quantity(2)
                .build();

        CreateMenuCommand stubMenuData3 = CreateMenuCommand.builder()
                .menuId("00000000-0000-0000-0000-000000000003")
                .quantity(4)
                .build();

        CreateOrderCommand stubOrderData = CreateOrderCommand.builder()
                .restaurantId("11111111-1111-1111-1111-111111111111")
                .menus(List.of(stubMenuData1, stubMenuData2, stubMenuData3))
                .deliveryAddress("서울시 강남구 어딘가 1-1")
                .build();

        PaymentRequestDto stubPaymentRequest = new PaymentRequestDto(
                PayType.CARD.name(),
                Company.SAMSUNG.name(),
                "1234-5678-1234-5678",
                48000
        );

        User user = new User();
        user.setId(2L);
        user.setUsername("hong1234");
        user.setName("홍길동");
        user.setPhone("010-1111-1111");
        user.setRole(UserRoleEnum.CUSTOMER);

        Restaurant restaurant = Restaurant.builder()
                .restaurantId("11111111-1111-1111-1111-111111111111")
                .name("맛있는 집")
                .phone("070-1234-5678")
                .address("서울시 중구 어딘가 2-2")
                .description("맛있는 음식점 입니다.")
                .status(RestaurantStatus.OPEN)
                .build();

        Order order = Order.builder()
                .user(user)
                .restaurant(restaurant)
                .orderedAt(LocalDateTime.of(2025, 10, 16, 0, 0, 0))
                .orderStatus(OrderStatus.PAYMENT_PENDING)
                .address("경기도 수원시 영통구 어딘가 1-1")
                .totalPrice(new BigDecimal("48000"))
                .build();

        ReflectionTestUtils.setField(order, "orderId", "8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8");
        ReflectionTestUtils.setField(order, "version", 0L);

        Order freshOrder = Order.builder()
                .user(user)
                .restaurant(restaurant)
                .orderedAt(LocalDateTime.of(2025, 10, 16, 0, 3, 0))
                .orderStatus(OrderStatus.ORDER_FAIL)
                .address("경기도 수원시 영통구 어딘가 1-1")
                .totalPrice(new BigDecimal("48000"))
                .build();

        ReflectionTestUtils.setField(freshOrder, "orderId", "8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8");
        ReflectionTestUtils.setField(freshOrder, "updatedAt", LocalDateTime.of(2025, 10, 16, 0, 3, 0));
        ReflectionTestUtils.setField(freshOrder, "version", 1L);

        Mockito.when(orderService.createOrderForPayment(Mockito.any(), Mockito.any()))
                .thenReturn(order);

        Mockito.when(paymentService.delegateRequest(stubPaymentRequest))
                .thenThrow(new PaymentException(PaymentResponseCode.INVALID_COMPANY));

        Mockito.when(orderService.failIfValid("8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8", 0L))
                .thenReturn(1);

        Mockito.when(orderService.loadFresh("8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8"))
                .thenReturn(freshOrder);

        // when
        OrderPaymentResponse response = orderPaymentService.checkout(stubOrderData, stubPaymentRequest, 1L);

        // then
        Assertions.assertThat(response.getRequiredId()).isEqualTo("8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8");
        Assertions.assertThat(response.getMessage()).isEqualTo("결제에 실패했습니다.");
        Assertions.assertThat(response.getPaymentResponseDto().getPaymentId()).isEqualTo("NO_DATA");
        Assertions.assertThat(response.getPaymentResponseDto().getCardNum()).isEqualTo("****-****-****-5678");
    }

    @DisplayName("주문 결제 실행 도중 결제 쪽에서 문제가 발생하면 실패 응답을 포함한 OrderPaymentResponse를 반환한다.")
    @Test
    void checkoutTest2() {
        // given
        Clock fixedClock = Clock.fixed(
                LocalDateTime.of(2025, 10, 10, 12, 3, 0)
                        .toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
        );

        Mockito.when(clock.instant()).thenReturn(fixedClock.instant());
        Mockito.when(clock.getZone()).thenReturn(fixedClock.getZone());

        CreateMenuCommand stubMenuData1 = CreateMenuCommand.builder()
                .menuId("00000000-0000-0000-0000-000000000001")
                .quantity(1)
                .build();

        CreateMenuCommand stubMenuData2 = CreateMenuCommand.builder()
                .menuId("00000000-0000-0000-0000-000000000002")
                .quantity(2)
                .build();

        CreateMenuCommand stubMenuData3 = CreateMenuCommand.builder()
                .menuId("00000000-0000-0000-0000-000000000003")
                .quantity(4)
                .build();

        CreateOrderCommand stubOrderData = CreateOrderCommand.builder()
                .restaurantId("11111111-1111-1111-1111-111111111111")
                .menus(List.of(stubMenuData1, stubMenuData2, stubMenuData3))
                .deliveryAddress("서울시 강남구 어딘가 1-1")
                .build();

        PaymentRequestDto stubPaymentRequest = new PaymentRequestDto(
                PayType.CARD.name(),
                Company.SAMSUNG.name(),
                "1234-5678-1234-5678",
                48000
        );

        User user = new User();
        user.setId(2L);
        user.setUsername("hong1234");
        user.setName("홍길동");
        user.setPhone("010-1111-1111");
        user.setRole(UserRoleEnum.CUSTOMER);

        Restaurant restaurant = Restaurant.builder()
                .restaurantId("11111111-1111-1111-1111-111111111111")
                .name("맛있는 집")
                .phone("070-1234-5678")
                .address("서울시 중구 어딘가 2-2")
                .description("맛있는 음식점 입니다.")
                .status(RestaurantStatus.OPEN)
                .build();

        Order order = Order.builder()
                .user(user)
                .restaurant(restaurant)
                .orderedAt(LocalDateTime.of(2025, 10, 16, 0, 0, 0))
                .orderStatus(OrderStatus.PAYMENT_PENDING)
                .address("경기도 수원시 영통구 어딘가 1-1")
                .totalPrice(new BigDecimal("48000"))
                .build();

        ReflectionTestUtils.setField(order, "orderId", "8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8");
        ReflectionTestUtils.setField(order, "updatedAt", LocalDateTime.of(2025, 10, 16, 0, 3, 0));
        ReflectionTestUtils.setField(order, "version", 1L);

        ObjenesisStd objenesis = new ObjenesisStd();
        Payment payment = objenesis.newInstance(Payment.class);

        ReflectionTestUtils.setField(payment, "paymentId", "0001");
        ReflectionTestUtils.setField(payment, "cardCompany", "SAMSUNG");
        ReflectionTestUtils.setField(payment, "payType", PayType.CARD);
        ReflectionTestUtils.setField(payment, "cardNum", "1234-5678-1234-5678");
        ReflectionTestUtils.setField(payment, "payState", PayState.CANCELED);
        ReflectionTestUtils.setField(payment, "paidAt", ZonedDateTime.now(clock));

        Order freshOrder = Order.builder()
                .user(user)
                .restaurant(restaurant)
                .orderedAt(LocalDateTime.of(2025, 10, 16, 0, 3, 0))
                .orderStatus(OrderStatus.ORDER_FAIL)
                .address("경기도 수원시 영통구 어딘가 1-1")
                .totalPrice(new BigDecimal("48000"))
                .build();

        ReflectionTestUtils.setField(freshOrder, "orderId", "8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8");
        ReflectionTestUtils.setField(freshOrder, "updatedAt", LocalDateTime.of(2025, 10, 16, 0, 3, 0));
        ReflectionTestUtils.setField(freshOrder, "version", 1L);

        Mockito.when(orderService.createOrderForPayment(Mockito.any(), Mockito.any()))
                .thenReturn(order);

        Mockito.when(paymentService.delegateRequest(stubPaymentRequest))
                .thenReturn(payment);

        Mockito.when(orderService.completeIfValid(order.getOrderId(), payment.getPaymentId(), 1L))
                .thenReturn(0);

        ReflectionTestUtils.setField(payment, "payState", PayState.CANCELED);
        Mockito.when(paymentService.paymentCancel(order))
                .thenReturn(payment);

        Mockito.when(orderService.failIfValid("8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8", 1L))
                .thenReturn(1);

        Mockito.when(orderService.loadFresh("8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8"))
                .thenReturn(freshOrder);

        // when // then
        OrderPaymentResponse response = orderPaymentService.checkout(stubOrderData, stubPaymentRequest, 2L);

        Assertions.assertThat(response.getRequiredId()).isEqualTo("8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8");
        Assertions.assertThat(response.getMessage()).isEqualTo("결제에 실패했습니다.");
        Assertions.assertThat(response.getPaymentResponseDto().getCardNum()).isEqualTo("****-****-****-5678");
    }

    @DisplayName("주문 결제 실행시 문제가 발생하지 않는 다면 '결제가 완료되었습니다.' 메시지를 가진 OrderPaymentResponse 응답")
    @Test
    void checkoutTest3() {
        // given
        Clock fixedClock = Clock.fixed(
                LocalDateTime.of(2025, 10, 10, 12, 3, 0)
                        .toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
        );

        Mockito.when(clock.instant()).thenReturn(fixedClock.instant());
        Mockito.when(clock.getZone()).thenReturn(fixedClock.getZone());

        CreateMenuCommand stubMenuData1 = CreateMenuCommand.builder()
                .menuId("00000000-0000-0000-0000-000000000001")
                .quantity(1)
                .build();

        CreateMenuCommand stubMenuData2 = CreateMenuCommand.builder()
                .menuId("00000000-0000-0000-0000-000000000002")
                .quantity(2)
                .build();

        CreateMenuCommand stubMenuData3 = CreateMenuCommand.builder()
                .menuId("00000000-0000-0000-0000-000000000003")
                .quantity(4)
                .build();

        CreateOrderCommand stubOrderData = CreateOrderCommand.builder()
                .restaurantId("11111111-1111-1111-1111-111111111111")
                .menus(List.of(stubMenuData1, stubMenuData2, stubMenuData3))
                .deliveryAddress("서울시 강남구 어딘가 1-1")
                .build();

        PaymentRequestDto stubPaymentRequest = new PaymentRequestDto(
                PayType.CARD.name(),
                Company.SAMSUNG.name(),
                "1234-5678-1234-5678",
                48000
        );

        User user = new User();
        user.setId(2L);
        user.setUsername("hong1234");
        user.setName("홍길동");
        user.setPhone("010-1111-1111");
        user.setRole(UserRoleEnum.CUSTOMER);

        Restaurant restaurant = Restaurant.builder()
                .restaurantId("11111111-1111-1111-1111-111111111111")
                .name("맛있는 집")
                .phone("070-1234-5678")
                .address("서울시 중구 어딘가 2-2")
                .description("맛있는 음식점 입니다.")
                .status(RestaurantStatus.OPEN)
                .build();

        Order order = Order.builder()
                .user(user)
                .restaurant(restaurant)
                .orderedAt(LocalDateTime.of(2025, 10, 16, 0, 0, 0))
                .orderStatus(OrderStatus.PAYMENT_PENDING)
                .address("경기도 수원시 영통구 어딘가 1-1")
                .totalPrice(new BigDecimal("48000"))
                .build();

        ReflectionTestUtils.setField(order, "orderId", "8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8");
        ReflectionTestUtils.setField(order, "updatedAt", LocalDateTime.of(2025, 10, 16, 0, 3, 0));
        ReflectionTestUtils.setField(order, "version", 1L);

        ObjenesisStd objenesis = new ObjenesisStd();
        Payment payment = objenesis.newInstance(Payment.class);

        ReflectionTestUtils.setField(payment, "paymentId", "0001");
        ReflectionTestUtils.setField(payment, "cardCompany", "SAMSUNG");
        ReflectionTestUtils.setField(payment, "payType", PayType.CARD);
        ReflectionTestUtils.setField(payment, "cardNum", "1234-5678-1234-5678");
        ReflectionTestUtils.setField(payment, "payState", PayState.COMPLETED);
        ReflectionTestUtils.setField(payment, "paidAt", ZonedDateTime.now(clock));

        Order freshOrder = Order.builder()
                .user(user)
                .restaurant(restaurant)
                .orderedAt(LocalDateTime.of(2025, 10, 16, 0, 3, 0))
                .orderStatus(OrderStatus.ORDER_FAIL)
                .address("경기도 수원시 영통구 어딘가 1-1")
                .totalPrice(new BigDecimal("48000"))
                .build();

        ReflectionTestUtils.setField(freshOrder, "orderId", "8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8");
        ReflectionTestUtils.setField(freshOrder, "updatedAt", LocalDateTime.of(2025, 10, 16, 0, 3, 0));
        ReflectionTestUtils.setField(freshOrder, "version", 2L);

        Mockito.when(orderService.createOrderForPayment(Mockito.any(), Mockito.any()))
                .thenReturn(order);

        Mockito.when(paymentService.delegateRequest(stubPaymentRequest))
                .thenReturn(payment);

        Mockito.when(orderService.completeIfValid(order.getOrderId(), payment.getPaymentId(), 1L))
                .thenReturn(1);

        Mockito.when(orderService.loadFresh("8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8"))
                .thenReturn(freshOrder);

        // when // then
        OrderPaymentResponse response = orderPaymentService.checkout(stubOrderData, stubPaymentRequest, 1L);

        Assertions.assertThat(response.getRequiredId()).isEqualTo("8a4f0b9d-b46a-4d2a-8d5a-7f4f0b3b65a8");
        Assertions.assertThat(response.getMessage()).isEqualTo("결제가 완료되었습니다.");
        Assertions.assertThat(response.getPaymentResponseDto().getCardNum()).isEqualTo("****-****-****-5678");
    }
}