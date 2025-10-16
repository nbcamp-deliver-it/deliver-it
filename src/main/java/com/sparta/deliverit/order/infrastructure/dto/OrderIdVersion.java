package com.sparta.deliverit.order.infrastructure.dto;

import java.time.LocalDateTime;

public interface OrderIdVersion {
    String getOrderId();
    Long getVersion();
    LocalDateTime getOrderedAt();
}
