package com.elice.holo.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderProductRequestDto {
    private Long productId;
    private int quantity;
    private String color;
    private String size;
}