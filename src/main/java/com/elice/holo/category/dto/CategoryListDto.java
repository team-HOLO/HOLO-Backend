package com.elice.holo.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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