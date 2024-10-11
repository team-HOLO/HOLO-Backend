package com.elice.holo.order.dto;

import lombok.Data;

@Data
public class OrderProductDto {

    private Long orderProductId;
    private Long productId;
    private int count;

    public OrderProductDto(Long orderProductId, Long productId, int count) {
        this.orderProductId = orderProductId;
        this.productId = productId;
        this.count = count;
    }
}