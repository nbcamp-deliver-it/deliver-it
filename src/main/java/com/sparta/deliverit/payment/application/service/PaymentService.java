package com.sparta.deliverit.payment.application.service;

import com.sparta.deliverit.order.domain.entity.Order;
import com.sparta.deliverit.payment.domain.entity.Payment;
import com.sparta.deliverit.payment.presentation.dto.PaymentRequestDto;
import com.sparta.deliverit.payment.presentation.dto.PaymentResponseDto;

public interface PaymentService {

    Payment delegateRequest(PaymentRequestDto requestDto);

    Payment paymentCancel(Order order);

    PaymentResponseDto deletePayment(Order order);
}
