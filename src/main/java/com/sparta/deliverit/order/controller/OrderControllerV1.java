package com.sparta.deliverit.order.controller;

import com.sparta.deliverit.common.dto.Result;
import com.sparta.deliverit.order.dto.response.*;

import org.springframework.web.bind.annotation.*;

@RestController
public class OrderControllerV1 implements OrderController{

    @GetMapping("/v1/orders")
    public Result<OrderListResponse> getOrderList() {
        return null;
    }
}
