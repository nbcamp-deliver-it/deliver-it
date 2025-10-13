package com.sparta.deliverit.global.exception;

import com.sparta.deliverit.global.response.code.ReviewResponseCode;

public class ReviewException extends DomainException {
    public ReviewException(ReviewResponseCode responseCode) {
        super(responseCode);
    }
}
