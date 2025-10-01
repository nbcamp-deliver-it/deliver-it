package com.sparta.deliverit.exception;

import com.sparta.deliverit.response.ApiResponse;
import com.sparta.deliverit.response.code.ClientResponseCode;
import com.sparta.deliverit.response.code.ServerResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {

        log.error(e.getMessage());

        ApiResponse<?> response = new ApiResponse<>(
                ServerResponseCode.SERVER_ERROR,
                "서버 오류가 발생했습니다.",
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e
    ) {
        log.error(e.getMessage());

        ApiResponse<?> response = new ApiResponse<>(
                ClientResponseCode.BAD_REQUEST,
                "요청 파라미터 누락입니다.",
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error(e.getMessage());

        ApiResponse<?> response = new ApiResponse<>(
                ClientResponseCode.NOT_FOUND,
                "존재하지 않는 URL 접근입니다.",
                null
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        log.error(e.getMessage());

        ApiResponse<?> response = new ApiResponse<>(
                ClientResponseCode.BAD_REQUEST,
                "필드 유효성 검증 실패입니다.",
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e
    ) {
        log.error(e.getMessage());

        ApiResponse<?> response = new ApiResponse<>(
                ClientResponseCode.BAD_REQUEST,
                "파라미터 타입 변환에 실패했습니다.",
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e
    ) {
        log.error(e.getMessage());

        ApiResponse<?> response = new ApiResponse<>(
                ClientResponseCode.BAD_REQUEST,
                "잘못된 요청 데이터이거나 파싱 실패입니다.",
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
