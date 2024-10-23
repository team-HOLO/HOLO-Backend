package com.elice.holo.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// sub category 전달용 DTO
@Getter
@AllArgsConstructor
public class CategoryDto {

    private Long categoryId;
    private String name;
}
