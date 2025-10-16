package com.sparta.deliverit.global.exception;

import com.sparta.deliverit.global.response.code.AiResponseCode;
import lombok.Getter;

@Getter
public class AiException extends DomainException {
    public AiException(AiResponseCode responseCode) {
        super(responseCode);
    }
}
