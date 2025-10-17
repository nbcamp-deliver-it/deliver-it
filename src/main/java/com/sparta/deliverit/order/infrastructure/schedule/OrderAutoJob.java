package com.sparta.deliverit.order.infrastructure.schedule;

import com.sparta.deliverit.order.application.service.OrderPaymentService;
import com.sparta.deliverit.order.infrastructure.dto.OrderIdVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Slf4j(topic = "OrderAutoJob")
@Service
public class OrderAutoJob {

    private final int TIME_OUT_MINUTE = 5;
    private final int SCHEDULE_DATA_RANGE_HOURS = 1;
    private final int PAGE_SIZE = 100;
    private final int MAX_LOOP = 200;
    private final int MAX_EXCEPT = 10;

    private final OrderPaymentService orderPaymentService;
    private final Clock clock;

    @Autowired
    public OrderAutoJob(OrderPaymentService orderPaymentService, Clock clock) {
        this.orderPaymentService = orderPaymentService;
        this.clock = clock;
    }

    @Scheduled(fixedDelay = 60_000)
    public void run() {
        LocalDateTime now = LocalDateTime.now(clock);
        int loopCount = 0;
        int exceptionCount = 0;
        int consecutiveFailCount = 0;
        int updateCount = 0;

        while (true) {

            if (loopCount >= MAX_LOOP || exceptionCount >= MAX_EXCEPT) break;
            loopCount++;

            Integer updated = null;

            try {
                updated = cancelOnePage(now);

            } catch (Exception e) {
                exceptionCount++;
                continue;
            }

            if (updated == -1) {
                break;
            }

            if (updated == 0) {
                // 모든 페이지에 대해서 처리되지 않는 경우가 반복되는 경우 확인
                consecutiveFailCount++;
                if (consecutiveFailCount >= 3) break;
            } else {
                consecutiveFailCount = 0;
                updateCount += updated;
            }
        }

        log.info("Auto cancelOrder updateCount : updateCount={}", updateCount);
    }

    public int cancelOnePage(LocalDateTime now) {
        Page<OrderIdVersion> page = orderPaymentService.findExpiredOrderIds(
                now.minusMinutes(TIME_OUT_MINUTE),
                now.minusHours(SCHEDULE_DATA_RANGE_HOURS),
                PageRequest.of(0, PAGE_SIZE)
        );

        if (page.isEmpty()) return -1;

        int updateCount = 0;

        for (OrderIdVersion orderIdVersion : page) {
            String orderId = orderIdVersion.getOrderId();
            try {
                int result = orderPaymentService.cancelOrderPayment(orderId);
                if (result > 0) updateCount += result;
            } catch (Exception e) {
                log.error("cancelOrder fail orderId={} message={}", orderId, e.getMessage());
                throw e;
            }
        }
        return updateCount;
    }
}

