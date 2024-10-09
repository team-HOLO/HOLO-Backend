package com.elice.holo.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CartProductDto {
    private  Long cartProductId; // 필드명 수정
    private  Long cartId;
    private  Long productId;
    private  Long quantity;


    }

