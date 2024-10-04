package com.elice.holo.category.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryResponseDto {

    private Long categoryId;
    private String name;
    private List<CategoryDto> subCategories;
}
