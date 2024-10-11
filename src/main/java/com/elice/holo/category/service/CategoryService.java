package com.elice.holo.category.service;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryListDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import com.elice.holo.category.exception.CategoryNotFoundException;
import com.elice.holo.category.exception.DuplicateCategoryNameException;
import com.elice.holo.category.mapper.CategoryMapper;
import com.elice.holo.category.repository.CategoryRepository;
import com.elice.holo.common.exception.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
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

    // 카테고리 삭제를 위한 메서드
    @Transactional
    public void deleteCategory(Long id) {

        Category target = categoryRepository.findByCategoryIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new CategoryNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                "해당 ID의 카테고리가 존재하지 않습니다."));

        target.deleteCategory();
        categoryRepository.save(target);
    }

    // 모든 카테고리 목록을 반환
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findByIsDeletedFalse();
        return categories.stream()
            .map(categoryMapper::toCategoryResponseDto)
            .collect(Collectors.toList());
    }

    // 카테고리 상세 정보 조회하는 메서드
    public CategoryDetailsDto getCategoryById(Long id) {
        Category category = categoryRepository.findByCategoryIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new CategoryNotFoundException(ErrorCode.CATEGORY_NOT_FOUND,
                "해당 ID의 카테고리가 존재하지 않습니다."));

        return categoryMapper.toCategoryDetailsDto(category);
    }


    // 최상위 카테고리 목록을 반환하는 메서드
    public List<CategoryResponseDto> getTopLevelCategories() {
        List<Category> topLevelCategories = categoryRepository.findByIsDeletedFalseAndParentCategoryIsNull();
        return topLevelCategories.stream()
            .map(categoryMapper::toCategoryResponseDto)
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
        // 정렬 기준
        List<Order> orders = new ArrayList<>();

        if (sortBy.equalsIgnoreCase("createdAt")) {
            orders.add(new Order(
                direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                "createdAt"));
        } else if (sortBy.equalsIgnoreCase("updatedAt")) {
            orders.add(new Order(
                direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                "updatedAt"));
        } else if (sortBy.equalsIgnoreCase("name")) {
            orders.add(new Order(
                direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                "name"));
        } else if (sortBy.equalsIgnoreCase("categoryId")) {
            orders.add(new Order(
                direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                "categoryId"));
        } else {
            // 기본 정렬 기준 설정 (예: 이름)
            orders.add(new Order(Sort.Direction.ASC, "name"));
        }

        Sort sort = Sort.by(orders);    // 정렬
        Pageable pageable = PageRequest.of(page, size, sort);       // pageable 객체 생성

        Page<Category> categoryPage;
        // 검색어가 있는 경우
        if (name != null && !name.isEmpty()) {
            categoryPage = categoryRepository.findByIsDeletedFalseAndNameContainingIgnoreCase(name,
                pageable);
        } else {        // 검색어가 없는 경우 -> 단순 반환
            categoryPage = categoryRepository.findByIsDeletedFalse(pageable);
        }

        return categoryPage.map(categoryMapper::toCategoryListDto);
    }
}
