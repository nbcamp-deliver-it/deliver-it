package com.sparta.deliverit.order.presentation.controller;

import com.sparta.deliverit.global.presentation.dto.Result;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice(assignableTypes = OrderControllerV1.class)
public class TemporaryOrderControllerV1Advice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Map<String, Object>>> handleBodyValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> Map.of("field", err.getField(), "message", err.getDefaultMessage()))
                .toList();

        Result<Map<String, Object>> result = Result.of(
                "잘못된 요청입니다.",
                "400",   // 여기서 비즈니스 에러 코드 표현
                Map.of("details", details)
        );

        return ResponseEntity.ok(result); // 항상 200
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Map<String, Object>>> handleParamValidation(ConstraintViolationException ex) {
        List<Map<String, String>> details = ex.getConstraintViolations().stream()
                .map(v -> Map.of("param", v.getPropertyPath().toString(), "message", v.getMessage()))
                .toList();

        Result<Map<String, Object>> result = Result.of(
                "잘못된 요청입니다.",
                "400",
                Map.of("details", details)
        );

        return ResponseEntity.ok(result); // 항상 200
    }
}
