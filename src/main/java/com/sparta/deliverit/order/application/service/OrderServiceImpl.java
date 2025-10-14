package com.sparta.deliverit.order.application.service;

import com.sparta.deliverit.payment.application.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final PaymentService paymentService;

    @Autowired
    public OrderServiceImpl(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
