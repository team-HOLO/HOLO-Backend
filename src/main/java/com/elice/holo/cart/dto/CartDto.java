package com.elice.holo.cart.dto;


import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CartDto {

    private Long cartId;
    private Long memberId;
    private List<CartProductDto> products;
    private double totalPrice;

    public CartDto() {
    }

    public CartDto(Long cartId, List<CartProductDto> products, double totalPrice) {
        this.cartId = cartId;
        this.products = products;
        this.totalPrice = totalPrice;
    }


}
