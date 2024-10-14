package com.elice.holo.product.dto;

import com.elice.holo.product.domain.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Data;

//상품 옵션 리스트 DTO
@Data
@AllArgsConstructor
public class ProductOptionDto {

    private String color;
    private String size;
    private int optionQuantity;

    public ProductOptionDto(ProductOption productOption) {
        color = productOption.getColor();
        size = productOption.getSize();
        optionQuantity = productOption.getOptionQuantity();
    }

    public ProductOption toEntity() {
        return ProductOption.createOption(color, size, optionQuantity);
    }

}
