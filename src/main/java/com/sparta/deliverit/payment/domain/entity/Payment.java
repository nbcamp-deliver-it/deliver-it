package com.sparta.deliverit.payment.domain.entity;

import com.sparta.deliverit.payment.enums.PayType;
import com.sparta.deliverit.payment.presentation.dto.PaymentRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_payment")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment {

    @Id
    @Column(name = "payment_id")
    private String paymentId;

    @NotEmpty
    @Column(name = "card_num")
    private String cardNum;

    @NotEmpty
    @Column(name = "card_company")
    private String cardCompany;

    @Enumerated(EnumType.STRING)
    private PayType payType;

    @Builder.Default
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "paid_at")
    private ZonedDateTime paidAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

    public static Payment of(PaymentRequestDto requestDto) {
        return Payment.builder()
                .paymentId(UUID.randomUUID().toString().substring(0, 12))
                .cardNum(requestDto.getCardNum())
                .cardCompany(requestDto.getCompany())
                .payType(PayType.valueOf(requestDto.getPayType()))
                .build();
    }

}
