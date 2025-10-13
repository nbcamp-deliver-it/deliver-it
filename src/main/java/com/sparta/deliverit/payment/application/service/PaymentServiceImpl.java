package com.sparta.deliverit.payment.application.service;

import com.sparta.deliverit.global.exception.PaymentException;
import com.sparta.deliverit.global.response.code.PaymentResponseCode;
import com.sparta.deliverit.order.domain.entity.Order;
import com.sparta.deliverit.payment.domain.entity.Payment;
import com.sparta.deliverit.payment.domain.repository.PaymentRepository;
import com.sparta.deliverit.payment.enums.Company;
import com.sparta.deliverit.payment.presentation.dto.PaymentRequestDto;
import com.sparta.deliverit.payment.presentation.dto.PaymentResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final List<PaymentProcessor> processorList;

    private final String INVALID_CARD_NUMBER = "9999-9999-9999-9999";
    private final String LIMIT_OVER_CARD_NUMBER = "8888-8888-8888-8888";

    public Payment delegateRequest(PaymentRequestDto requestDto) {
        isErrorCard(requestDto.getCardNum());

        PaymentProcessor processor = getProcessor(Company.of(requestDto.getCompany()));
        Payment entity = processor.paymentRequest(requestDto);

        return paymentRepository.save(entity);
    }

    public Payment paymentCancel(Order order) {
        Payment payment = paymentRepository.findById(order.getPayment()).orElseThrow();
        payment.cancel();

        return payment;
    }

    public PaymentResponseDto deletePayment(Order order) {
        Payment payment = paymentRepository.findById(order.getPayment()).orElseThrow();
        paymentRepository.delete(payment);
        return PaymentResponseDto.of(payment);
    }

    private PaymentProcessor getProcessor(Company company) {
        return processorList.stream()
                .filter(processor -> processor.findByCompany(company))
                .findFirst()
                .orElseThrow(() -> new PaymentException(PaymentResponseCode.INVALID_COMPANY));
    }

    private void isErrorCard(String cardNum) {
        if(cardNum.equals(INVALID_CARD_NUMBER))
            throw new PaymentException(PaymentResponseCode.INVALID_CARD_NUMBER);
        else if(cardNum.equals(LIMIT_OVER_CARD_NUMBER))
            throw new PaymentException(PaymentResponseCode.CARD_LIMIT_EXCEEDED);
    }
}
