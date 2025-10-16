//package com.sparta.deliverit.payment.application.service;
//
//import com.sparta.deliverit.global.exception.PaymentException;
//import com.sparta.deliverit.global.response.code.PaymentResponseCode;
//import com.sparta.deliverit.order.domain.entity.Order;
//import com.sparta.deliverit.order.infrastructure.OrderRepository;
//import com.sparta.deliverit.payment.application.service.card.KbCardProcessor;
//import com.sparta.deliverit.payment.application.service.card.SamSungCardProcessor;
//import com.sparta.deliverit.payment.domain.entity.Payment;
//import com.sparta.deliverit.payment.domain.repository.PaymentRepository;
//import com.sparta.deliverit.payment.enums.Company;
//import com.sparta.deliverit.payment.application.service.dto.PaymentRequestDto;
//import com.sparta.deliverit.payment.enums.PayState;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.BDDMockito.any;
//import static org.mockito.BDDMockito.given;
//
//@ExtendWith(MockitoExtension.class)
//class PaymentServiceImplTest {
//
//    @Mock
//    private PaymentRepository paymentRepository;
//
//    @Mock
//    private OrderRepository orderRepository;
//
//    @InjectMocks
//    private PaymentServiceImpl service;
//
//    @Spy
//    private List<PaymentProcessor> processorList = List.of(
//            new KbCardProcessor(), new SamSungCardProcessor()
//    );
//
//    @ParameterizedTest
//    @ValueSource(strings = {"삼성", "KB"})
//    @DisplayName("현재 지원되는 카드들의 결제성공")
//    void delegateRequest(String companyName) {
//        //given
//        PaymentRequestDto requestDto = new PaymentRequestDto
//                ("카드", companyName, "1111-1111-1111-1111", 10000);
//        Payment payment = Payment.of(requestDto, Company.of(companyName));
//        given(paymentRepository.save(any(Payment.class))).willReturn(payment);
//
//        //when
//        Payment result = service.delegateRequest(requestDto);
//
//        //then
//        assertThat(payment.getPaymentId()).isEqualTo(result.getPaymentId());
//        assertThat(payment.getCompany()).isEqualTo(result.getCompany());
//    }
//
//    @Test
//    @DisplayName("현재 지원되지 않는 카드로 시도시, 결제실패")
//    void delegateRequest_Fail() {
//        //given
//        String notSupportCompany = "지원하지않는카드사";
//        PaymentRequestDto requestDto = new PaymentRequestDto
//                ("카드", notSupportCompany, "1111-1111-1111-1111", 10000);
//
//        //when
//        assertThatThrownBy(() -> service.delegateRequest(requestDto))
//
//        //then
//                .isExactlyInstanceOf(PaymentException.class)
//                .hasMessage(PaymentResponseCode.INVALID_COMPANY.getMessage());
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {"9999-9999-9999-9999", "8888-8888-8888-8888"})
//    @DisplayName("카드 정보 에러로 인한 결제실패")
//    void delegateRequest_Fail_CardError(String cardNum) {
//        //given
//        PaymentRequestDto requestDto = new PaymentRequestDto
//                ("카드", "삼성", cardNum, 10000);
//
//        //when
//        assertThatThrownBy(() -> service.delegateRequest(requestDto))
//
//        //then
//                .isExactlyInstanceOf(PaymentException.class);
//
//    }
//
//    @Test
//    @DisplayName("결제취소시, Payment객체의 상태변화 성공")
//    void paymentCancel() {
//        //given
//        PaymentRequestDto requestDto = new PaymentRequestDto
//                ("카드", "삼성", "1111-1111-1111-1111", 10000);
//        Payment payment = Payment.of(requestDto, Company.SAMSUNG);
//        Order order = Order.builder().payment(payment).build();
//
//        given(paymentRepository.findById(any())).willReturn(Optional.of(payment));
//        given(orderRepository.findById(anyString())).willReturn(Optional.of(order));
//
//        //when
//        Payment result = service.paymentCancel(order);
//
//        //then
//        assertThat(result.getPayState() == PayState.COMPLETED);
//        assertThat(payment.getPayState() != result.getPayState());
//    }
//}