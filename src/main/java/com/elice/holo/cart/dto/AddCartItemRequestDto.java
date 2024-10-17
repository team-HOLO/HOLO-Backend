package com.elice.holo.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemRequestDto {

    @NotNull
    private Long productId;
    @NotNull
    @Positive
    private Long quantity;
    private String color;
    private String size;


}
