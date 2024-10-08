package com.elice.holo.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CategoryCreateDto {

    private String name;
    private String description;
    private Long parentCategory;
}
