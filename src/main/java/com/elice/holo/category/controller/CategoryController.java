package com.elice.holo.category.controller;

import com.elice.holo.category.dto.CategoryResponseDto;
import com.elice.holo.category.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // 전체 카테고리 목록을 조회
    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categoryList = categoryService.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(categoryList);
    }

    // 최상위 카테고리 목록 조회
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getTopLevelCategories() {
        List<CategoryResponseDto> topCategories = categoryService.getTopLevelCategories();
        return ResponseEntity.status(HttpStatus.OK).body(topCategories);
    }

    // 특정 카테고리의 하위 카테고리 목록 조회
    @GetMapping("/sub/{id}")
    public ResponseEntity<List<CategoryResponseDto>> getSubCategories(
        @PathVariable(name = "id") Long id) {
        List<CategoryResponseDto> subCategories = categoryService.getSubCategories(id);
        return ResponseEntity.status(HttpStatus.OK).body(subCategories);
    }

}
