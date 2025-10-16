package com.sparta.deliverit.payment.application.service.card;

import com.sparta.deliverit.payment.application.service.PaymentProcessor;
import com.sparta.deliverit.payment.domain.entity.Payment;
import com.sparta.deliverit.payment.enums.Company;
import com.sparta.deliverit.payment.application.service.dto.PaymentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SamSungCardProcessor implements PaymentProcessor {

    @Override
    public Payment paymentProcessing(PaymentRequestDto requestDto) {
        return Payment.of(requestDto, Company.SAMSUNG);
    }

    @Override
    public boolean findByCompany(Company company) {
        return company == Company.SAMSUNG;
    }
}
