package com.elice.holo.cart.dto;

import lombok.Getter;


@Getter
public class CartProductDto {
    private  Long cartProductId; // 필드명 수정
    private  Long cartId;
    private  Long productId;
    private  Long quantity;

}
