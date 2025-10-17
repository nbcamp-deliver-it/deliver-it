package com.sparta.deliverit.global.exception;

import com.sparta.deliverit.global.response.code.ResponseCode;

public class MapException extends DomainException {
    public MapException(ResponseCode responseCode) {
        super(responseCode);
    }
}

