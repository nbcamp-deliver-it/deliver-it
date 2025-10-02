package com.sparta.deliverit.payment.presentation.dto;

import com.sparta.deliverit.payment.domain.entity.Payment;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentResponseDto {

    private String paymentId;

    private String cardNum;

    private String cardCompany;

    private String paidAt;


    public static PaymentResponseDto of(Payment payment) {
        return new PaymentResponseDto(
                payment.getPaymentId(),
                payment.getCardNum(),
                payment.getCardCompany(),
                payment.getPaidAt().toString()
        );
    }
}
