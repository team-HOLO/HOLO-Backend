package com.elice.holo.cart.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CartDto {

    private Long cartId;
    private Long memberId;
    private List<CartProductDto> products;



}
