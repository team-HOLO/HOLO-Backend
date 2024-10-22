package com.elice.holo.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private int price;

    @NotBlank
    private String description;

    @NotNull
    private int stockQuantity;

    private Long categoryId;

    private List<UpdateProductOptionDto> productOptions;

    private List<Boolean> isThumbnails;

}
