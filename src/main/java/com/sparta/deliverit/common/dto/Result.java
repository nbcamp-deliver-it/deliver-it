package com.sparta.deliverit.common.dto;

import lombok.Getter;

@Getter
public class Result<T> {

    private final String message;
    private final String responseCode;
    private final T data;

    private Result(String message, String responseCode, T data) {
        this.message = message;
        this.responseCode = responseCode;
        this.data = data;
    }

    public static <T> Result<T> of(String message, String responseCode, T data) {
        return new Result(message, responseCode, data);
    }
}

