package com.elice.holo.category.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryResponseDto {

    private Long categoryId;
    private String name;
    private List<CategoryDto> subCategories;
}
