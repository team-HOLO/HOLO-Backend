package com.elice.holo.product.dto;

import com.elice.holo.product.domain.ProductOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//상품 옵션 수정 DTO
@Data
@AllArgsConstructor
public class UpdateProductOptionDto {

    private Long id;
    private String color;
    private String size;
    private int optionQuantity;

    public UpdateProductOptionDto(ProductOption productOption) {
        color = productOption.getColor();
        size = productOption.getSize();
        optionQuantity = productOption.getOptionQuantity();
    }

    public ProductOption toEntity() {
        return ProductOption.createOption(color, size, optionQuantity);
    }
}
