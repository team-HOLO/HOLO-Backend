package com.elice.holo.category.dto;

import com.elice.holo.category.domain.Category;
import lombok.Data;

@Data
public class CategoryCreateDto {
    private String name;
    private String description;
    private Long parentCategory;
}
