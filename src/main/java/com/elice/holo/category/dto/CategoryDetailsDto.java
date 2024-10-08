package com.elice.holo.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryDetailsDto {

    private Long categoryId;
    private String name;
    private String description;
    private CategoryDto parentCategory;
}
