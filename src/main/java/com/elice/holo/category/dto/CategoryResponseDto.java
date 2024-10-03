package com.elice.holo.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryResponseDto {
    private Long id;
    private String name;
    private List<CategoryDto> subCategories;
}
