package com.sparta.deliverit.payment.enums;

import lombok.Getter;

public enum PayType {
    CARD("카드");

    @Getter
    private final String type;

    PayType(String type) {
        this.type = type;
    }
}
