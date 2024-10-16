package com.elice.holo.product.dto;

import com.elice.holo.product.domain.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

//상품 등록 request DTO
@Data
@AllArgsConstructor
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

    public Product toEntity() {
        return Product.createProduct(name, price, description, stockQuantity);
    }


}
