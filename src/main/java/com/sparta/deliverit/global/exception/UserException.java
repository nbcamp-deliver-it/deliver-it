package com.sparta.deliverit.global.exception;

import com.sparta.deliverit.global.response.code.UserResponseCode;

public class UserException extends DomainException {
    public UserException(UserResponseCode responseCode) {
        super(responseCode);
    }
}
