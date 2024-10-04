package com.elice.holo.category.dto;

import lombok.Data;

@Data
public class CategoryCreateDto {

    private String name;
    private String description;
    private Long parentCategory;
}
