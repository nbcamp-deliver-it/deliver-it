package com.sparta.deliverit.payment.application.service;

import com.sparta.deliverit.payment.presentation.dto.PaymentRequestDto;
import com.sparta.deliverit.payment.presentation.dto.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto delegateRequest(String orderId, PaymentRequestDto requestDto);

    PaymentResponseDto getPayment(String orderId, String paymentId);

    PaymentResponseDto deletePayment(String orderId, String paymentId);
}
