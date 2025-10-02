package com.sparta.deliverit.payment.domain.repository;

import com.sparta.deliverit.payment.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
