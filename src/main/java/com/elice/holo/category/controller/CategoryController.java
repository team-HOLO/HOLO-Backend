package com.elice.holo.category.controller;

import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import com.elice.holo.category.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    public final CategoryService categoryService;

    @PostMapping
    public CategoryResponseDto createCategory(@RequestBody CategoryCreateDto categoryCreateDto) {
        return categoryService.createCategory(categoryCreateDto);
    }

    @GetMapping
    public List<CategoryResponseDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/about/{id}")
    public CategoryDetailsDto getCategoryDetails(@PathVariable(name = "id") Long id) {
        return categoryService.getCategoryById(id);
    }

}
