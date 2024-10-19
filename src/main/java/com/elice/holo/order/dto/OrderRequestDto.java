package com.elice.holo.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    private List<OrderProductRequestDto> products;
    private String shippingAddress;
}