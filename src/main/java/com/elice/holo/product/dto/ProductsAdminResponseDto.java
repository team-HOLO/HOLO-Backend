package com.elice.holo.product.dto;

import com.elice.holo.product.domain.Product;
import lombok.Data;

//관리자 페이지용 DTO
@Data
public class ProductsAdminResponseDto {

    private Long productId;
    private String name;
    private String description;
    private Boolean isDeleted;

    public ProductsAdminResponseDto(Product product) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.isDeleted = product.getIsDeleted();
    }

    public ProductsAdminResponseDto(Long productId, String name, String description,
        Boolean isDeleted) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.isDeleted = isDeleted;
    }
}
