package com.elice.holo.order.dto;

import lombok.Data;

@Data
public class OrderProductRequestDto {
    private Long productId;
    private int quantity;
    private String color;
    private String size;
}