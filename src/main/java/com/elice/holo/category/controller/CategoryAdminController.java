package com.elice.holo.category.controller;

import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryListDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import com.elice.holo.category.service.CategoryService;
import com.elice.holo.member.domain.MemberDetails;
import com.elice.holo.member.exception.AccessDeniedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    // 특정 카테고리의 상세 정보 조회
    @GetMapping("/details/{id}")
    public ResponseEntity<CategoryDetailsDto> getCategoryDetails(
        @PathVariable(name = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 카테고리 상세정보 조회 권한이 있는지 확인
        if (!memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("카테고리 상세 정보 조회 권한이 없습니다.");
        }
        CategoryDetailsDto categoryDetails = categoryService.getCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(categoryDetails);
    }

    // 새로운 카테고리 생성
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
        @Valid @RequestBody CategoryCreateDto categoryCreateDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 카테고리 생성 권한이 있는지 확인
        if (!memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("카테고리 생성 권한이 없습니다.");
        }
        CategoryResponseDto categoryResponseDto = categoryService.createCategory(categoryCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDto);
    }


    // 카테고리 정보 수정(업데이트)
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
        @Valid @RequestBody CategoryCreateDto categoryCreateDto,
        @PathVariable(name = "id") Long id
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 카테고리 수정 권한이 있는지 확인
        if (!memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("카테고리 수정 권한이 없습니다.");
        }

        CategoryResponseDto updated = categoryService.updateCategory(id, categoryCreateDto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);

    }

    // 카테고리 삭제 처리(Soft Delete 적용)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable(name = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 카테고리 삭제 권한이 있는지 확인
        if (!memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("카테고리 삭제 권한이 없습니다.");
        }

        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Admin 카테고리 관리 페이지에서 페이지네이션 및 검색을 적용한 카테고리 전체 목록 조회
    @GetMapping
    public ResponseEntity<Page<CategoryListDto>> getCategories(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "15") int size,
        @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,
        @RequestParam(name = "direction", defaultValue = "asc") String direction,
        @RequestParam(name = "name", required = false) String name) {

        Page<CategoryListDto> categoryPage = categoryService.getCategoriesPageable(page, size,
            sortBy, direction, name);
        return ResponseEntity.status(HttpStatus.OK).body(categoryPage);
    }

}
