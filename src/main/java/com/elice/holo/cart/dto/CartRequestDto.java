package com.elice.holo.cart.dto;

import lombok.Data;


@Data
public class CartRequestDto {

    private Long productId;  // 상품 ID
    private int quantity;     // 수량
    private String color;     // 색상
    private String size;

    public CartRequestDto(Long productId, int quantity, String color, String size) {
        this.productId = productId;
        this.quantity = quantity;
        this.color = color;
        this.size = size;
    }// 사이즈

}
