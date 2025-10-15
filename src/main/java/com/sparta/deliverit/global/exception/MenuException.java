package com.sparta.deliverit.global.exception;

import com.sparta.deliverit.global.response.code.ResponseCode;

public class MenuException extends DomainException{
    public MenuException(ResponseCode responseCode) {
        super(responseCode);
    }
}
