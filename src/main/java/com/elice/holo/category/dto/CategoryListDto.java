package com.elice.holo.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 카테고리 관리 페이지에서 목록 조회를 위한 DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryListDto {

    private Long categoryId;
    private Long parentId;
    private String name;
    private String description;
}