package com.elice.holo.product.controller.dto;

import com.elice.holo.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class ProductResponseDto {

    private Long id;
    private String name;
    private int price;
    private String description;

//    private List<ProductImageDto>  TODO 상품 이미지 반환

    public ProductResponseDto(Product product) {
        new ProductResponseDto(
            product.getId(), product.getName(), product.getPrice(), product.getDescription()
        );
    }

}
