package com.sparta.deliverit.order.infrastructure;

import com.sparta.deliverit.order.domain.entity.OrderStatus;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForOwner;
import com.sparta.deliverit.order.infrastructure.dto.OrderDetailForUser;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;


@DataJpaTest
@ActiveProfiles("mysql")
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Sql(
        scripts = {"/sql/user_restaurant_insert.sql", "/sql/menu_insert.sql", "/sql/order_insert.sql", "/sql/orderitem_insert.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
public class OrderRepositoryTestForMysql {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PlatformTransactionManager txManager;

    @Autowired
    EntityManager em;

    @DisplayName("특정 주문에 대하여 동시에 확인을 요청하는 경우 둘 중 하나는 성공하고 하나는 실패한다.")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void test() throws Exception {

        OrderDetailForOwner currentOrder = orderRepository.getByOrderIdForOwner("00000000-0000-0000-0000-000000000003").orElseThrow();

        System.out.println("currentOrder.toString() = " + currentOrder.toString());
        final String orderId = currentOrder.getOrderId();
        final String restaurantId = currentOrder.getRestaurantId();
        final Long restaurantUserId = Long.valueOf(currentOrder.getRestaurantUserId());
        final OrderStatus currentStatus = currentOrder.getOrderStatus();
        final OrderStatus nextStatus = OrderStatus.ORDER_CONFIRMED;
        final Long beforeVersion = currentOrder.getVersion();

        LocalDateTime cutOffTime = LocalDateTime.of(2025,10,10,12,3,0).minusMinutes(5);

        TransactionTemplate txNew = new TransactionTemplate(txManager);
        txNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        CountDownLatch latch = new CountDownLatch(1);

        Callable<Integer> task = () -> {
            latch.await(); // 동시에 시작
            return txNew.execute(status -> {
                // 여기 안은 완전 별도 트랜잭션
                return orderRepository.updateOrderStatusToConfirm(
                        orderId,
                        restaurantId,
                        restaurantUserId,
                        currentStatus,
                        nextStatus,
                        beforeVersion,
                        cutOffTime
                );
            });
        };

        ExecutorService es = Executors.newFixedThreadPool(2);
        Future<Integer> f1 = es.submit(task);
        Future<Integer> f2 = es.submit(task);

        latch.countDown(); // 동시에 실행
        es.shutdown();

        int result1 = f1.get();
        int result2 = f2.get();
        Assertions.assertThat(result1 + result2).isEqualTo(1);

        em.clear();

        OrderDetailForOwner nextOrder = orderRepository.getByOrderIdForOwner("00000000-0000-0000-0000-000000000003").orElseThrow();

        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion + 1);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.ORDER_CONFIRMED);
    }

    @DisplayName("특정 주문에 대하여 동시에 취소을 요청하는 경우 둘 중 하나는 성공하고 하나는 실패한다.")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void updateOrderStatusToCancelForUserWithConcurrency() throws Exception {

        OrderDetailForUser currentOrder = orderRepository.getByOrderIdForUser("00000000-0000-0000-0000-000000000004").orElseThrow();

        System.out.println("currentOrder.toString() = " + currentOrder.toString());
        final String orderId = currentOrder.getOrderId();
        final String orderUserId = currentOrder.getUserId();
        final OrderStatus currentStatus = OrderStatus.ORDER_COMPLETED;
        final OrderStatus nextStatus = OrderStatus.ORDER_CANCELED;
        final Long beforeVersion = currentOrder.getVersion();

        LocalDateTime cutOffTime = LocalDateTime.of(2025,10,10,12,3,0).minusMinutes(5);

        TransactionTemplate txNew = new TransactionTemplate(txManager);
        txNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        CountDownLatch latch = new CountDownLatch(1);

        Callable<Integer> task = () -> {
            latch.await(); // 동시에 시작
            return txNew.execute(status -> {
                // 여기 안은 완전 별도 트랜잭션
                return orderRepository.updateOrderStatusToCancelForUser(
                        orderId,
                        Long.valueOf(orderUserId),
                        currentStatus,
                        nextStatus,
                        beforeVersion,
                        cutOffTime
                );
            });
        };

        ExecutorService es = Executors.newFixedThreadPool(2);
        Future<Integer> f1 = es.submit(task);
        Future<Integer> f2 = es.submit(task);

        latch.countDown(); // 동시에 실행
        es.shutdown();

        int result1 = f1.get();
        int result2 = f2.get();
        Assertions.assertThat(result1 + result2).isEqualTo(1);

        em.clear();

        OrderDetailForUser nextOrder = orderRepository.getByOrderIdForUser("00000000-0000-0000-0000-000000000004").orElseThrow();

        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion + 1);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.ORDER_CANCELED);
    }

    @DisplayName("특정 주문에 대하여 동시에 확인, 취소 요청하는 경우 둘 중 하나는 성공하고 하나는 실패한다.")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void updateOrderStatusToCancelForUserWithupdateOrderStatusConfirm() throws Exception {

        OrderDetailForOwner currentOrderForOwner = orderRepository.getByOrderIdForOwner("00000000-0000-0000-0000-000000000004").orElseThrow();

        System.out.println("currentOrder.toString() = " + currentOrderForOwner.toString());
        final String orderIdForOwner = currentOrderForOwner.getOrderId();
        final String restaurantIdForOwner = currentOrderForOwner.getRestaurantId();
        final Long restaurantUserIdForOwner = Long.valueOf(currentOrderForOwner.getRestaurantUserId());
        final OrderStatus currentStatusForOwner = OrderStatus.ORDER_COMPLETED;
        final OrderStatus nextStatusForOwner = OrderStatus.ORDER_CONFIRMED;
        final Long beforeVersionForOwner = currentOrderForOwner.getVersion();


        OrderDetailForUser currentOrderForUser = orderRepository.getByOrderIdForUser("00000000-0000-0000-0000-000000000004").orElseThrow();

        System.out.println("currentOrder.toString() = " + currentOrderForUser.toString());
        final String orderIdForUser = currentOrderForUser.getOrderId();
        final String orderUserIdForUser = currentOrderForUser.getUserId();
        final OrderStatus currentStatusForUser = OrderStatus.ORDER_COMPLETED;
        final OrderStatus nextStatusForUser = OrderStatus.ORDER_CANCELED;
        final Long beforeVersionForUser = currentOrderForUser.getVersion();

        LocalDateTime cutOffTime = LocalDateTime.of(2025,10,10,12,3,0).minusMinutes(5);

        TransactionTemplate txNew = new TransactionTemplate(txManager);
        txNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        CountDownLatch latch = new CountDownLatch(1);

        Callable<Integer> updateOrderStatusToConfirm = () -> {
            latch.await(); // 동시에 시작
            return txNew.execute(status -> {
                // 여기 안은 완전 별도 트랜잭션
                return orderRepository.updateOrderStatusToConfirm(
                        orderIdForOwner,
                        restaurantIdForOwner,
                        restaurantUserIdForOwner,
                        currentStatusForOwner,
                        nextStatusForOwner,
                        beforeVersionForOwner,
                        cutOffTime
                );
            });
        };

        Callable<Integer> updateOrderStatusToCancel = () -> {
            latch.await(); // 동시에 시작
            return txNew.execute(status -> {
                // 여기 안은 완전 별도 트랜잭션
                return orderRepository.updateOrderStatusToCancelForUser(
                        orderIdForUser,
                        Long.valueOf(orderUserIdForUser),
                        currentStatusForUser,
                        nextStatusForUser,
                        beforeVersionForUser,
                        cutOffTime
                );
            });
        };

        ExecutorService es = Executors.newFixedThreadPool(2);
        Future<Integer> f1 = es.submit(updateOrderStatusToConfirm);
        Future<Integer> f2 = es.submit(updateOrderStatusToCancel);

        latch.countDown(); // 동시에 실행
        es.shutdown();

        int result1 = f1.get();
        int result2 = f2.get();
        Assertions.assertThat(result1 + result2).isEqualTo(1);

        em.clear();

        OrderDetailForUser nextOrder = orderRepository.getByOrderIdForUser("00000000-0000-0000-0000-000000000004").orElseThrow();

        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersionForUser + 1);
        Assertions.assertThat(nextOrder.getOrderStatus()).isIn(OrderStatus.ORDER_CONFIRMED, OrderStatus.ORDER_CANCELED);
    }

    @DisplayName("음식점에서 특정 주문에 대하여 동시에 취소을 요청하는 경우 둘 중 하나는 성공하고 하나는 실패한다.")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void updateOrderStatusToCancelForOwnerWithConcurrency() throws Exception {

        OrderDetailForOwner currentOrder = orderRepository.getByOrderIdForOwner("00000000-0000-0000-0000-000000000004").orElseThrow();

        System.out.println("currentOrder.toString() = " + currentOrder.toString());
        final String orderId = currentOrder.getOrderId();
        final String restaurantId = "11111111-1111-1111-1111-111111111111";
        final Long accessUserId = 2L;
        final List<OrderStatus> currentOrderStatusList = List.of(OrderStatus.ORDER_COMPLETED, OrderStatus.ORDER_CONFIRMED);
        final OrderStatus nextStatus = OrderStatus.ORDER_CANCELED;
        final Long beforeVersion = currentOrder.getVersion();

        TransactionTemplate txNew = new TransactionTemplate(txManager);
        txNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        CountDownLatch latch = new CountDownLatch(1);

        Callable<Integer> task = () -> {
            latch.await(); // 동시에 시작
            return txNew.execute(status -> {
                // 여기 안은 완전 별도 트랜잭션
                return orderRepository.updateOrderStatusToCancelForOwner(
                        orderId,
                        restaurantId,
                        accessUserId,
                        currentOrderStatusList,
                        nextStatus,
                        beforeVersion
                );
            });
        };

        ExecutorService es = Executors.newFixedThreadPool(2);
        Future<Integer> f1 = es.submit(task);
        Future<Integer> f2 = es.submit(task);

        latch.countDown(); // 동시에 실행
        es.shutdown();

        int result1 = f1.get();
        int result2 = f2.get();
        Assertions.assertThat(result1 + result2).isEqualTo(1);

        em.clear();

        OrderDetailForUser nextOrder = orderRepository.getByOrderIdForUser("00000000-0000-0000-0000-000000000004").orElseThrow();

        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersion + 1);
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.ORDER_CANCELED);
    }

    @DisplayName("음식 점주가 특정 주문에 대하여 동시에 확인, 취소 요청하는 경우 둘 중 하나는 성공하고 하나는 실패한다.")
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void updateOrderStatusToCancelForOwnerWithupdateOrderStatusConfirm() throws Exception {

        OrderDetailForOwner currentOrderForOwner = orderRepository.getByOrderIdForOwner("00000000-0000-0000-0000-000000000004").orElseThrow();

        System.out.println("currentOrder.toString() = " + currentOrderForOwner.toString());
        final String orderIdForOwner = currentOrderForOwner.getOrderId();
        final String restaurantIdForOwner = currentOrderForOwner.getRestaurantId();
        final Long restaurantUserIdForOwner = Long.valueOf(currentOrderForOwner.getRestaurantUserId());
        final Long beforeVersionForOwner = currentOrderForOwner.getVersion();

        LocalDateTime cutOffTime = LocalDateTime.of(2025,10,10,12,3,0).minusMinutes(5);

        TransactionTemplate txNew = new TransactionTemplate(txManager);
        txNew.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        CountDownLatch latch = new CountDownLatch(1);

        Callable<Integer> updateOrderStatusToConfirm = () -> {
            latch.await(); // 동시에 시작
            return txNew.execute(status -> {
                // 여기 안은 완전 별도 트랜잭션
                return orderRepository.updateOrderStatusToConfirm(
                        orderIdForOwner,
                        restaurantIdForOwner,
                        restaurantUserIdForOwner,
                        OrderStatus.ORDER_COMPLETED,
                        OrderStatus.ORDER_CONFIRMED,
                        beforeVersionForOwner,
                        cutOffTime
                );
            });
        };

        Callable<Integer> updateOrderStatusToCancel = () -> {
            latch.await(); // 동시에 시작
            return txNew.execute(status -> {
                // 여기 안은 완전 별도 트랜잭션
                return orderRepository.updateOrderStatusToCancelForOwner(
                        orderIdForOwner,
                        restaurantIdForOwner,
                        restaurantUserIdForOwner,
                        List.of(OrderStatus.ORDER_COMPLETED, OrderStatus.ORDER_CONFIRMED),
                        OrderStatus.ORDER_CANCELED,
                        beforeVersionForOwner
                );
            });
        };

        ExecutorService es = Executors.newFixedThreadPool(2);
        Future<Integer> f1 = es.submit(updateOrderStatusToConfirm);
        Future<Integer> f2 = es.submit(updateOrderStatusToCancel);

        latch.countDown(); // 동시에 실행
        es.shutdown();

        int result1 = f1.get();
        int result2 = f2.get();
        Assertions.assertThat(result1 + result2).isEqualTo(1);

        em.clear();

        OrderDetailForUser nextOrder = orderRepository.getByOrderIdForUser("00000000-0000-0000-0000-000000000004").orElseThrow();

        Assertions.assertThat(nextOrder.getVersion()).isEqualTo(beforeVersionForOwner + 1);
        Assertions.assertThat(nextOrder.getOrderStatus()).isIn(OrderStatus.ORDER_CONFIRMED, OrderStatus.ORDER_CANCELED);
    }
}
