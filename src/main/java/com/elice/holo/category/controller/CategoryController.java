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

    /**
     * 전체 카테고리 목록을 조회
     *
     * @return ResponseEntity로 감싼 전체 카테고리 목록. 정상 처리: HTTP 상태 코드는 200 OK / 예외 발생: 400 Bad Request
     */
    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        try {
            List<CategoryResponseDto> categoryList = categoryService.getAllCategories();
            return ResponseEntity.status(HttpStatus.OK).body(categoryList);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 최상위 카테고리 목록 조회
     *
     * @return ResponseEntity로 감싼 하위 카테고리 목록. 정상 처리: HTTP 상태 코드는 200 OK / 예외 발생: 404 Not Found
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getTopLevelCategories() {
        try {
            List<CategoryResponseDto> topCategories = categoryService.getTopLevelCategories();
            return ResponseEntity.status(HttpStatus.OK).body(topCategories);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 특정 카테고리의 하위 카테고리 목록 조회
     *
     * @param id 하위 카테고리를 조회할 상위 카테고리의 id
     * @return ResponseEntity로 감싼 하위 카테고리 목록. 정상 처리: HTTP 상태 코드는 200 OK / 예외 발생: 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<List<CategoryResponseDto>> getSubCategories(
        @PathVariable(name = "id") Long id) {
        try {
            List<CategoryResponseDto> subCategories = categoryService.getSubCategories(id);
            return ResponseEntity.status(HttpStatus.OK).body(subCategories);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 특정 카테고리의 상세 정보 조회
     *
     * @param id 조회하고자 하는 카테고리의 id
     * @return ResponseEntity로 감싼 특정 카테고리 정보. 정상 처리: HTTP 상태 코드는 200 OK / 예외 발생: 404 Not Found
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<CategoryDetailsDto> getCategoryDetails(
        @PathVariable(name = "id") Long id) {
        try {
            CategoryDetailsDto categoryDetails = categoryService.getCategoryById(id);
            return ResponseEntity.status(HttpStatus.OK).body(categoryDetails);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 새로운 카테고리 생성
     *
     * @param categoryCreateDto 새로 생성할 카테고리의 정보
     * @return ResponseEntity로 감싼 생성된 카테고리의 정보. 정상 처리: HTTP 상태 코드 201 Created / 예외 발생: 400 Bad
     * Request
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
        @RequestBody CategoryCreateDto categoryCreateDto) {
        try {
            CategoryResponseDto categoryResponseDto = categoryService.createCategory(
                categoryCreateDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDto);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * 카테고리 정보 수정(업데이트)
     *
     * @param categoryCreateDto 수정할 카테고리 정보
     * @param id                수정 대상 카테고리의 id
     * @return ResponseEntity로 감싼 업데이트된 카테고리의 정보. 정상처리: HTTP 상태코드 200 OK / 예외 발생: 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
        @RequestBody CategoryCreateDto categoryCreateDto,
        @PathVariable Long id
    ) {
        try {
            CategoryResponseDto updated = categoryService.updateCategory(id, categoryCreateDto);
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 카테고리 삭제 처리(Soft Delete 적용)
     *
     * @param id 삭제할 대상 카테고리의 아이디
     * @return HTTP 상태코드 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable(name = "id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
