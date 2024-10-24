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
import org.springdoc.core.annotations.ParameterObject;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @Operation(summary = "특정 카테고리의 상세 정보 조회", description = "주어진 ID에 해당하는 카테고리의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "카테고리 상세 정보 조회 성공"),
        @ApiResponse(responseCode = "403", description = "접근 거부"),
        @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @GetMapping("/details/{id}")
    public ResponseEntity<CategoryDetailsDto> getCategoryDetails(
        @Parameter(description = "카테고리 ID") @PathVariable(name = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        if (!memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("카테고리 상세 정보 조회 권한이 없습니다.");
        }
        CategoryDetailsDto categoryDetails = categoryService.getCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(categoryDetails);
    }

    @Operation(summary = "새로운 카테고리 생성", description = "주어진 정보를 바탕으로 새로운 카테고리를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "카테고리 생성 성공"),
        @ApiResponse(responseCode = "403", description = "접근 거부")
    })
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
        @Valid @RequestBody CategoryCreateDto categoryCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        if (!memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("카테고리 생성 권한이 없습니다.");
        }
        CategoryResponseDto categoryResponseDto = categoryService.createCategory(categoryCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDto);
    }

    @Operation(summary = "카테고리 정보 수정", description = "주어진 ID에 해당하는 카테고리를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
        @ApiResponse(responseCode = "403", description = "접근 거부"),
        @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
        @Valid @RequestBody CategoryCreateDto categoryCreateDto,
        @Parameter(description = "카테고리 ID") @PathVariable(name = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        if (!memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("카테고리 수정 권한이 없습니다.");
        }

        CategoryResponseDto updated = categoryService.updateCategory(id, categoryCreateDto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @Operation(summary = "카테고리 삭제", description = "주어진 ID에 해당하는 카테고리를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "카테고리 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "접근 거부"),
        @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
        @Parameter(description = "카테고리 ID") @PathVariable(name = "id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        if (!memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("카테고리 삭제 권한이 없습니다.");
        }

        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "카테고리 목록 조회", description = "페이지네이션 및 검색을 적용하여 카테고리 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<Page<CategoryListDto>> getCategories(
        @Parameter(description = "페이지 번호", required = false, example = "0")
        @RequestParam(name = "page", defaultValue = "0") int page,

        @Parameter(description = "페이지 크기", required = false, example = "15")
        @RequestParam(name = "size", defaultValue = "15") int size,

        @Parameter(description = "정렬 기준", required = false, example = "name")
        @RequestParam(name = "sortBy", defaultValue = "name") String sortBy,

        @Parameter(description = "정렬 방향", required = false, example = "asc")
        @RequestParam(name = "direction", defaultValue = "asc") String direction,

        @Parameter(description = "카테고리 이름으로 검색", required = false)
        @RequestParam(name = "name", required = false) String name) {

        Page<CategoryListDto> categoryPage = categoryService.getCategoriesPageable(page, size, sortBy, direction, name);
        return ResponseEntity.status(HttpStatus.OK).body(categoryPage);
    }
}
