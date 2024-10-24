package com.elice.holo.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 카테고리 상세 정보 전달 시
@Getter
@AllArgsConstructor
public class CategoryDetailsDto {

    private Long categoryId;
    private String name;
    private String description;
    private CategoryDto parentCategory;
}
