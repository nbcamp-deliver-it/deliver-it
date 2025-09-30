package com.sparta.deliverit.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.deliverit.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderInfo {
    @JsonProperty("order_id")
    private final String orderId;

    @JsonProperty("restaurant_name")
    private final String restaurantName;

    @JsonProperty("username")
    private final String username;

    @JsonProperty("order_time")
    private final String orderTime;

    @JsonProperty("order_status")
    private final String orderStatus;

    @JsonProperty("delivery_address")
    private final String deliveryAddress;

    @JsonProperty("menus")
    private final List<MenuInfo> menus;

    @JsonProperty("total_price")
    private final int totalPrice;

    @Builder
    private OrderInfo(String orderId, String restaurantName, String username, String orderTime, OrderStatus orderStatus, String deliveryAddress, List<MenuInfo> menus, int totalPrice) {
        this.orderId = orderId;
        this.restaurantName = restaurantName;
        this.username = username;
        this.orderTime = orderTime;
        this.orderStatus = orderStatus.getDescription();
        this.deliveryAddress = deliveryAddress;
        this.menus = menus;
        this.totalPrice = totalPrice;
    }
}
