package com.sparta.deliverit.response.code;

public interface ResponseCodeType {
    default String code() {
        return this.toString();
    }
}
