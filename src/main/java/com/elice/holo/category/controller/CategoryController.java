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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "전체 카테고리 목록 조회", description = "모든 카테고리의 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공")
    })
    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> categoryList = categoryService.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(categoryList);
    }

    @Operation(summary = "최상위 카테고리 목록 조회", description = "최상위 카테고리의 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "최상위 카테고리 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getTopLevelCategories() {
        List<CategoryResponseDto> topCategories = categoryService.getTopLevelCategories();
        return ResponseEntity.status(HttpStatus.OK).body(topCategories);
    }

    @Operation(summary = "하위 카테고리 목록 조회", description = "특정 카테고리의 하위 카테고리 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "하위 카테고리 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "해당 카테고리가 없음")
    })
    @GetMapping("/sub/{id}")
    public ResponseEntity<List<CategoryResponseDto>> getSubCategories(
        @Parameter(description = "카테고리 ID") @PathVariable(name = "id") Long id) {
        List<CategoryResponseDto> subCategories = categoryService.getSubCategories(id);
        return ResponseEntity.status(HttpStatus.OK).body(subCategories);
    }
}
