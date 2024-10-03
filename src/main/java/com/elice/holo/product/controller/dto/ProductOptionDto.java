package com.elice.holo.product.controller.dto;

import com.elice.holo.product.domain.ProductOption;
import lombok.Data;

//상품 옵션 리스트 DTO
@Data
public class ProductOptionDto {

    private String color;
    private String size;

    public ProductOptionDto(ProductOption productOption) {
        color = productOption.getColor();
        size = productOption.getSize();
    }

}
