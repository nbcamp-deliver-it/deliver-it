package com.sparta.deliverit.payment.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

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

    @Builder.Default
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "paid_at")
    private ZonedDateTime paidAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

}
