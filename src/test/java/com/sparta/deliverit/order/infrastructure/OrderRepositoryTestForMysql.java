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
        final OrderStatus nextStatus = OrderStatus.CONFIRMED;
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
        Assertions.assertThat(nextOrder.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }
}
