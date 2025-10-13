//package com.sparta.deliverit.payment.presentation.controller;
//
//import com.sparta.deliverit.payment.application.service.PaymentServiceImpl;
//import com.sparta.deliverit.payment.presentation.dto.PaymentRequestDto;
//import com.sparta.deliverit.payment.presentation.dto.PaymentResponseDto;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/v1")
//@Slf4j
//public class PaymentControllerV1 {
//
//    private final PaymentServiceImpl service;
//
//    @PostMapping("/{orderId}/payment")
//    @ResponseStatus(value = HttpStatus.CREATED)
//    public PaymentResponseDto payment(@PathVariable String orderId,
//                                      @Valid @RequestBody PaymentRequestDto requestDto) {
//        return PaymentResponseDto.of(service.delegateRequest(orderId, requestDto));
//    }
//
//    @GetMapping("/payment/{orderId}/{paymentId}")
//    @ResponseStatus(value = HttpStatus.OK)
//    public PaymentResponseDto getPayment(@PathVariable String orderId,
//                                         @PathVariable String paymentId) {
//        return service.getPayment(orderId, paymentId);
//    }
//
//    @DeleteMapping("/payment/{orderId}/{paymentId}")
//    @ResponseStatus(value = HttpStatus.OK)
//    public PaymentResponseDto deletePayment(@PathVariable String orderId,
//                                            @PathVariable String paymentId) {
//        return service.deletePayment(orderId, paymentId);
//    }
//}
