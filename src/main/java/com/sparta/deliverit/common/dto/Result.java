package com.sparta.deliverit.common.dto;

import lombok.Getter;

@Getter
public class Result<T> {

    private final String message;
    private final String code;
    private final T data;

    private Result(String message, String code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public static <T> Result<T> of(String message, String code, T data) {
        return new Result(message, code, data);
    }
}

