package com.sparta.deliverit.payment.application.service;

import com.sparta.deliverit.payment.domain.entity.Payment;
import com.sparta.deliverit.payment.enums.Company;
import com.sparta.deliverit.payment.presentation.dto.PaymentRequestDto;

public interface PaymentProcessor {

    Payment paymentRequest(PaymentRequestDto requestDto);

    boolean findByCompany(Company company);
}
