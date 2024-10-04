package com.elice.holo.product.service.dto;

import com.elice.holo.product.controller.dto.ProductOptionDto;
import com.elice.holo.product.domain.Product;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    @Pattern(regexp = "[0-9]*$") //숫자만 허용
    private int price;

    @NotBlank
    private String description;

    @NotBlank
    @Pattern(regexp = "[0-9]*$") //숫자만 허용
    private int stockQuantity;

    private List<ProductOptionDto> productOptions;

//    private List<MultipartFile> multipartFiles; //상품 이미지들 TODO

    public Product toEntity() {
        return Product.createProduct(name, price, description, stockQuantity);
    }

}
