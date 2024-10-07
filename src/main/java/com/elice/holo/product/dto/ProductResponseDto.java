package com.elice.holo.product.dto;

import com.elice.holo.product.domain.Product;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

//상품 단일 조회 DTO
@Data
@AllArgsConstructor
public class ProductResponseDto {

    private Long productId;
    private String name;
    private int price;
    private String description;
    private List<ProductOptionDto> productOptions;  //TODO Fetch JOIN 적용
//    private List<ProductImageDto>  TODO 상품 이미지 반환

    public ProductResponseDto(Product product) {
        productId = product.getProductId();
        name = product.getName();
        price = product.getPrice();
        description = product.getDescription();
        productOptions = product.getProductOptions().stream()
            .map(ProductOptionDto::new)
            .collect(Collectors.toList());
    }

}
