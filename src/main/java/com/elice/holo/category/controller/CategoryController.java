package com.elice.holo.category.controller;

import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import com.elice.holo.category.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @GetMapping("/{id}")
    public ResponseEntity<List<CategoryResponseDto>> getSubCategories(
        @PathVariable(name = "id") Long id) {
        List<CategoryResponseDto> subCategories = categoryService.getSubCategories(id);
        return ResponseEntity.status(HttpStatus.OK).body(subCategories);
    }

    // 특정 카테고리의 상세 정보 조회
    @GetMapping("/details/{id}")
    public ResponseEntity<CategoryDetailsDto> getCategoryDetails(
        @PathVariable(name = "id") Long id) {
        CategoryDetailsDto categoryDetails = categoryService.getCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(categoryDetails);
    }

    // 새로운 카테고리 생성
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
        @RequestBody CategoryCreateDto categoryCreateDto) {
        CategoryResponseDto categoryResponseDto = categoryService.createCategory(categoryCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDto);
    }


    // 카테고리 정보 수정(업데이트)
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
        @RequestBody CategoryCreateDto categoryCreateDto,
        @PathVariable(name = "id") Long id
    ) {
        CategoryResponseDto updated = categoryService.updateCategory(id, categoryCreateDto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);

    }

    // 카테고리 삭제 처리(Soft Delete 적용)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable(name = "id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
