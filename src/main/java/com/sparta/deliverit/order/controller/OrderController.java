package com.sparta.deliverit.order.controller;

import com.sparta.deliverit.common.dto.Result;
import com.sparta.deliverit.order.dto.response.OrderListResponse;

public interface OrderController {

    Result<OrderListResponse> getOrderList();

}
