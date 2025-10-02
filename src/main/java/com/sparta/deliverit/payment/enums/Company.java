package com.sparta.deliverit.payment.enums;

import lombok.Getter;

public enum Company {
    SAMSUNG("삼성"), KB("KB");

    @Getter
    private final String name;

    Company(String name) {
        this.name = name;
    }
}
