package com.sparta.deliverit.order.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CancelOrderInfo {
    private final String orderId;
    private final String previousStatus;
    private final String currentStatus;
    private final String cancelAt;

    @Builder
    private CancelOrderInfo(String orderId, String previousStatus, String currentStatus, String cancelAt) {
        this.orderId = orderId;
        this.previousStatus = previousStatus;
        this.currentStatus = currentStatus;
        this.cancelAt = cancelAt;
    }

    public static CancelOrderInfo of(String orderid, String previousStatus, String currentStatus, String cancelAt) {
        return CancelOrderInfo.builder()
                .orderId(orderid)
                .previousStatus(previousStatus)
                .currentStatus(currentStatus)
                .cancelAt(cancelAt)
                .build();
    }
}
