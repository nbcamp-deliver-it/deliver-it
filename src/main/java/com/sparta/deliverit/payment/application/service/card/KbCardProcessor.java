package com.sparta.deliverit.payment.application.service.card;

import com.sparta.deliverit.payment.application.service.PaymentProcessor;
import com.sparta.deliverit.payment.domain.entity.Payment;
import com.sparta.deliverit.payment.domain.repository.PaymentRepository;
import com.sparta.deliverit.payment.enums.Company;
import com.sparta.deliverit.payment.presentation.dto.PaymentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KbCardProcessor implements PaymentProcessor {

    @Override
    public Payment paymentProcessing(PaymentRequestDto requestDto) {
        return Payment.of(requestDto, Company.KB);
    }

    @Override
    public boolean findByCompany(Company company) {
        return company == Company.KB;
    }
}
