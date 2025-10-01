package com.sparta.deliverit.global.response.code;

public interface ResponseCodeType {
    default String code() {
        return this.toString();
    }
}
