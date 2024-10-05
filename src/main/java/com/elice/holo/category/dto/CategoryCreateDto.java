package com.elice.holo.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryCreateDto {

    private String name;
    private String description;
    private Long parentCategory;
}
