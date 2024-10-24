package com.elice.holo.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 카테고리 생성, 수정 요청 시
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CategoryCreateDto {

    @NotBlank(message = "카테고리 명은 필수입니다.")
    private String name;
    private String description;
    private Long parentCategory;
}
