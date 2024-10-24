package com.elice.holo.category.service;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryDto;
import com.elice.holo.category.dto.CategoryListDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import com.elice.holo.category.exception.CategoryNotFoundException;
import com.elice.holo.category.exception.DuplicateCategoryNameException;
import com.elice.holo.category.mapper.CategoryMapper;
import com.elice.holo.category.repository.CategoryRepository;
import com.elice.holo.common.exception.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    // 신규 카테고리 추가를 위한 메서드
    @Transactional
    public CategoryResponseDto createCategory(CategoryCreateDto categoryCreateDto) {
        // Category 생성자 기반 매핑
        Category category = categoryMapper.toEntity(categoryCreateDto);

        // 카테고리 중복 이름 확인
        if (categoryRepository.existsByNameAndIsDeletedFalse(categoryCreateDto.getName())) {
            throw new DuplicateCategoryNameException(ErrorCode.DUPLICATE_CATEGORY_NAME);
        }

        if (categoryCreateDto.getParentCategory() != null) {
            Category parentCategory = categoryRepository.findByCategoryIdAndIsDeletedFalse(
                    categoryCreateDto.getParentCategory())
                .orElseThrow(() -> new CategoryNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                    "해당 상위 카테고리는 존재하지 않습니다"));
            category.updateParentCategory(parentCategory);
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponseDto(savedCategory);
    }


    // 카테고리 수정을 위한 메서드
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryCreateDto updateDto) {
        Category targetCategory = categoryRepository.findByCategoryIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new CategoryNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                "해당 ID의 카테고리가 존재하지 않습니다."));

        // 카테고리 중복 이름 확인
        if (!targetCategory.getName().equals(updateDto.getName())
            && categoryRepository.existsByNameAndIsDeletedFalse(updateDto.getName())) {
            throw new DuplicateCategoryNameException(ErrorCode.DUPLICATE_CATEGORY_NAME);
        }

        Category parent = null;     // 상위 카테고리 초기값 null
        // 상위 카테고리 설정 시
        if (updateDto.getParentCategory() != null) {
            parent = categoryRepository.findByCategoryIdAndIsDeletedFalse(
                    updateDto.getParentCategory())
                .orElseThrow(() -> new CategoryNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                    "해당 상위 카테고리는 존재하지 않습니다."));
        }

        targetCategory.updateCategory(updateDto.getName(), updateDto.getDescription(), parent);

        Category updatedCategory = categoryRepository.save(targetCategory);
        return categoryMapper.toCategoryResponseDto(updatedCategory);
    }

    // 카테고리 삭제를 위한 메서드 (하위 카테고리들도 삭제)
    @Transactional
    public void deleteCategory(Long id) {
        Category target = categoryRepository.findByCategoryIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new CategoryNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                "해당 ID의 카테고리가 존재하지 않습니다."));

        // 하위 카테고리들도 삭제 (soft delete)
        deleteSubCategories(target);

        target.deleteCategory();
        categoryRepository.save(target);  // 삭제된 상태 저장
    }

    // 하위 카테고리 재귀적으로 삭제하는 메서드
    private void deleteSubCategories(Category parentCategory) {
        List<Category> subCategories = categoryRepository.findByParentCategoryAndIsDeletedFalse(
            parentCategory);

        for (Category subCategory : subCategories) {
            deleteSubCategories(subCategory);
            subCategory.deleteCategory();
            categoryRepository.save(subCategory);
        }
    }

    // 모든 카테고리 목록을 반환
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findByIsDeletedFalse();
        return getCategoryResponseDtosIsNotDeleted(categories);
    }

    // 카테고리 상세 정보 조회하는 메서드
    public CategoryDetailsDto getCategoryById(Long id) {
        Category category = categoryRepository.findByCategoryIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new CategoryNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                "해당 ID의 카테고리가 존재하지 않습니다."));

        return categoryMapper.toCategoryDetailsDto(category);
    }


    public List<CategoryResponseDto> getTopLevelCategories() {
        List<Category> topLevelCategories = categoryRepository.findByIsDeletedFalseAndParentCategoryIsNull();
        return getCategoryResponseDtosIsNotDeleted(topLevelCategories);
    }

    private List<CategoryResponseDto> getCategoryResponseDtosIsNotDeleted(
        List<Category> topLevelCategories) {
        return topLevelCategories.stream()
            .map(category -> {
                // 하위 카테고리 중 isDeleted가 false인 것만 포함
                List<CategoryDto> filteredSubCategories = category.getSubCategories().stream()
                    .filter(subCategory -> !Boolean.TRUE.equals(
                        subCategory.getIsDeleted())) // isDeleted가 true인 것 제외
                    .map(categoryMapper::toCategoryDto) // CategoryDto로 변환
                    .collect(Collectors.toList());

                // 필터링된 하위 카테고리를 포함하여 CategoryResponseDto 생성
                return new CategoryResponseDto(
                    category.getCategoryId(),
                    category.getName(),
                    filteredSubCategories // 필터링된 하위 카테고리 전달
                );
            })
            .collect(Collectors.toList());
    }


    // 타겟 카테고리의 하위 카테고리 목록을 반환하는 메서드
    public List<CategoryResponseDto> getSubCategories(Long id) {
        Category parentCategory = categoryRepository.findByCategoryIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new CategoryNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                "해당 ID의 대분류 카테고리가 존재하지 않습니다."));

        List<Category> subCategories = categoryRepository.findByParentCategoryAndIsDeletedFalse(
            parentCategory);
        return subCategories.stream()
            .map(categoryMapper::toCategoryResponseDto)
            .collect(Collectors.toList());
    }

    // 검색 기능 추가
    public Page<CategoryListDto> getCategoriesPageable(int page, int size, String sortBy,
        String direction, String name) {
        // 정렬 기준을 HashMap으로 처리
        Map<String, String> sortCriteria = new HashMap<>();
        sortCriteria.put("createdAt", "createdAt");
        sortCriteria.put("updatedAt", "updatedAt");
        sortCriteria.put("name", "name");
        sortCriteria.put("categoryId", "categoryId");

        // 정렬 속성 설정
        String sortProperty = sortCriteria.getOrDefault(sortBy, "name");
        Sort.Direction sortDirection =
            direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(new Sort.Order(sortDirection, sortProperty));

        Pageable pageable = PageRequest.of(page, size, sort);  // pageable 객체 생성

        Page<Category> categoryPage;
        // 검색어가 있는 경우
        if (name != null && !name.isEmpty()) {
            categoryPage = categoryRepository.findByIsDeletedFalseAndNameContainingIgnoreCase(name,
                pageable);
        } else {  // 검색어가 없는 경우
            categoryPage = categoryRepository.findByIsDeletedFalse(pageable);
        }

        return categoryPage.map(categoryMapper::toCategoryListDto);
    }
}
