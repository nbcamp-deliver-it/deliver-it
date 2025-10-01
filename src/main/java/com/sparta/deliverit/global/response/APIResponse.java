package com.sparta.deliverit.global.response;

public record APIResponse<T>(
        String code,
        String message,
        int status,
        T data
) {
    public static <T> APIResponse<T> createError(String code, String message, int status) {
        return new APIResponse<>(code, message, status, null);
    }
}
