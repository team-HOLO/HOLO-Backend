package com.elice.holo.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
//@AllArgsConstructor
public class AddCartItemRequestDto {

    private Long cartId;
    @NotNull
    private Long productId;
    @NotNull
    @Positive
    private Long quantity;
    private String color;
    private String size;

    public AddCartItemRequestDto(Long cartId, Long productId, Long quantity, String color,
        String size) {
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.color = color;
        this.size = size;
    }


}
