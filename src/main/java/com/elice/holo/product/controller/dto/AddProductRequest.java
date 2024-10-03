package com.elice.holo.product.controller.dto;

import com.elice.holo.product.domain.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddProductRequest {

    @NotBlank
    private String name;

    @NotBlank
    @Pattern(regexp = "[0-9]*$") //숫자만 허용
    private int price;

    @NotBlank
    private String description;

    @NotBlank
    @Pattern(regexp = "[0-9]*$") //숫자만 허용
    private int stockQuantity;

//    private List<MultipartFile> multipartFiles; //상품 이미지들 TODO

    public Product toEntity() {
        return Product.builder()
            .name(name)
            .price(price)
            .description(description)
            .stockQuantity(stockQuantity)
            .build();
    }

}
