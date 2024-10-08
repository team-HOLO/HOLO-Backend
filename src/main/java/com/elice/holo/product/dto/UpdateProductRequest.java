package com.elice.holo.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

//상품 수정 request dto
@Data
@AllArgsConstructor
public class UpdateProductRequest {

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

    private List<UpdateProductOptionDto> productOptions;

//    private List<MultipartFile> multipartFiles; //상품 이미지들 TODO
}
