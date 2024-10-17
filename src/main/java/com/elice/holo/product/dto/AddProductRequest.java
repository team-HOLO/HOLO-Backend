package com.elice.holo.product.dto;

import com.elice.holo.product.domain.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

//상품 등록 request DTO
@Data
public class AddProductRequest {

    @NotBlank
    private String name;

    @NotNull
    private int price;

    @NotBlank
    private String description;

    @NotNull
    private int stockQuantity;

    private List<ProductOptionDto> productOptions;

    private List<Boolean> isThumbnails;

    private Long categoryId;

    public Product toEntity() {
        return Product.createProduct(name, price, description, stockQuantity);
    }

    public AddProductRequest(String name, int price, String description, int stockQuantity,
        List<ProductOptionDto> productOptions, List<Boolean> isThumbnails) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.productOptions = productOptions;
        this.isThumbnails = isThumbnails;
    }
}
