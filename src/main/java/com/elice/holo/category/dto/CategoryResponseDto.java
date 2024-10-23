package com.elice.holo.category.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

// 카테고리 생성 이후 응답 DTO
@Getter
@AllArgsConstructor
public class CategoryResponseDto {

    private Long categoryId;
    private String name;
    private List<CategoryDto> subCategories;
}
