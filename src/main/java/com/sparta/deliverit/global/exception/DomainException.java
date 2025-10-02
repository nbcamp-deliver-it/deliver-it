package com.sparta.deliverit.global.exception;

import com.sparta.deliverit.global.response.code.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DomainException extends RuntimeException {
    private ResponseCode responseCode;
}