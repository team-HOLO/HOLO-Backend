package com.elice.holo.product.dto;

import com.elice.holo.product.domain.Product;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

//상품 단일 조회 DTO
@Data
public class ProductResponseDto {

    private Long productId;
    private String name;
    private int price;
    private String description;
    private List<ProductOptionDto> productOptions;
    private List<ProductImageDto>  productImageDtos;

    public ProductResponseDto(Product product) {
        productId = product.getProductId();
        name = product.getName();
        price = product.getPrice();
        description = product.getDescription();
        productOptions = product.getProductOptions().stream()
            .filter(po -> !po.isDeleted())
            .map(ProductOptionDto::new)
            .collect(Collectors.toList());
    }

}
