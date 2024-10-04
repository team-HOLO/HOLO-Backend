package com.elice.holo.order.dto;

import lombok.Data;

@Data
public class OrderProductDto {

    private Long orderProductId;
    private Long productId;
    private int count;
}