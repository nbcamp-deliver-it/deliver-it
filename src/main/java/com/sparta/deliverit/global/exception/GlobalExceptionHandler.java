package com.sparta.deliverit.global.exception;

import com.sparta.deliverit.global.response.APIResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
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
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse error = new ErrorResponse(
                ErrorCode.SERVER_ERROR,
                "서버 오류가 발생했습니다."
        );

        log.error(error.toString());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<APIResponse> handleDomainException(DomainException e) {
        log.error("DomainException message: {}", e.getErrorMessage(), e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        APIResponse.createError(
                                e.getErrorCode().name(),
                                e.getErrorMessage(),
                                HttpStatus.BAD_REQUEST.value()
                        )
                );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<APIResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("MissingServletRequestParameterException message: {}", e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        APIResponse.createError(
                                ErrorCode.MISSING_PARAMETER.name(),
                                e.getMessage(),
                                HttpStatus.BAD_REQUEST.value()
                        )
                );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<APIResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("NoResourceFoundException message: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        APIResponse.createError(
                                ErrorCode.INVALID_PATH.name(),
                                "요청한 리소스를 찾을 수 없습니다.",
                                HttpStatus.NOT_FOUND.value()
                        )
                );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException message: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        APIResponse.createError(
                                ErrorCode.INVALID_TYPE.name(),
                                e.getMessage(),
                                HttpStatus.BAD_REQUEST.value()
                        )
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException message: {}", e.getMessage(), e);
        val message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        APIResponse.createError(
                                ErrorCode.VALIDATION_FAILED.name(),
                                message,
                                HttpStatus.BAD_REQUEST.value()
                )
            );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("HttpMessageNotReadableException message: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        APIResponse.createError(
                                ErrorCode.UNREADABLE_MESSAGE.name(),
                                "요청 본문을 읽을 수 없습니다.",
                                HttpStatus.BAD_REQUEST.value()
                        )
                );
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<APIResponse> handleBindException(BindException e) {
        log.error("BindException message: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        APIResponse.createError(
                                ErrorCode.BINDING_FAILED.name(),
                                e.getMessage(),
                                HttpStatus.BAD_REQUEST.value()
                )
            );
    }
}
