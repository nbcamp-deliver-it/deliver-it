package com.sparta.deliverit.payment.domain.entity;

import com.sparta.deliverit.anything.entity.BaseEntity;
import com.sparta.deliverit.payment.enums.Company;
import com.sparta.deliverit.payment.enums.PayState;
import com.sparta.deliverit.payment.enums.PayType;
import com.sparta.deliverit.payment.presentation.dto.PaymentRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_payment")
@Getter
@Builder
@SQLDelete(sql = "UPDATE p_payment SET deleted_at = CURRENT_TIMESTAMP WHERE payment_id = ?")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment extends BaseEntity {

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

    @Enumerated(EnumType.STRING)
    private PayState payState;

    @Builder.Default
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "paid_at")
    private ZonedDateTime paidAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

    public static Payment of(PaymentRequestDto requestDto, Company company) {
        return Payment.builder()
                .paymentId(UUID.randomUUID().toString().substring(0, 12))
                .cardNum(requestDto.getCardNum())
                .cardCompany(company.getName())
                .payType(PayType.of(requestDto.getPayType()))
                .payState(PayState.COMPLETED)
                .build();
    }

    public Payment cancel() {
        this.payState = PayState.CANCELED;
        return this;
    }

}
