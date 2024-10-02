package com.elice.holo.category.service;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import com.elice.holo.category.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 카테고리 추가를 위한 메서드
     *
     * @param categoryCreateDto 생성할 카테고리의 정보가 담긴 dto
     * @return 생성 완료된 카테고리의 정보를 담음 response dto
     */
    public CategoryResponseDto createCategory(CategoryCreateDto categoryCreateDto) {
        Category category = new Category();
        // TODO: 추후에 Mapper 사용하도록 변경 예정
        category.setName(categoryCreateDto.getName());
        category.setDescription(categoryCreateDto.getDescription());
        if (categoryCreateDto.getParentCategory() != null) {
            category.setParentCategory(
                categoryRepository.findByCategoryIdAndIsDeletedFalse(
                        categoryCreateDto.getParentCategory())
                    .get());
        }

        Category savedCategory = categoryRepository.save(category);

        // TODO: 추후에 Mapper 사용하도록 변경
        return new CategoryResponseDto(savedCategory.getCategoryId(), savedCategory.getName(),
            null);
    }

    /**
     * 카테고리 정보 수정을 위한 메서드
     *
     * @param id        수정할 카테고리의 id
     * @param updateDto 수정할 내용이 담긴 CategoryCreateDto
     * @return 수정 내용이 반영된 CategoryResponseDto
     */
    public CategoryResponseDto updateCategory(Long id, CategoryCreateDto updateDto) {
        Optional<Category> optionalCategory = categoryRepository.findByCategoryIdAndIsDeletedFalse(
            id);

        if (optionalCategory.isPresent()) {
            Category targetCategory = optionalCategory.get();
            targetCategory.setName(updateDto.getName());
            targetCategory.setDescription(updateDto.getDescription());
            targetCategory.setParentCategory(
                categoryRepository.findByCategoryIdAndIsDeletedFalse(updateDto.getParentCategory())
                    .get());

            Category updatedCategory = categoryRepository.save(targetCategory);

            return new CategoryResponseDto(updatedCategory.getCategoryId(),
                updatedCategory.getName(), null);
        } else {
            throw new IllegalArgumentException("Category with id " + id + " not found");
        }
    }

    /**
     * 주어진 ID에 해당하는 카테고리를 삭제하는 메서드 카테고리가 존재하지 않을 경우 IllegalArgumentException 예외 발생
     *
     * @param id 삭제할 카테고리의 ID
     * @throws IllegalArgumentException 해당 ID의 카테고리가 존재하지 않을 경우 발생
     */
    public void deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다.");
        }
    }

    /**
     * sub category를 포함한 모든 카테고리 반환
     *
     * @return CategoryResponseDto 리스트
     */
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findByIsDeletedFalse();
        return categories.stream()
            .map(category -> new CategoryResponseDto(
                category.getCategoryId(),
                category.getName(),
                category.getSubCategories().stream()
                    .filter(
                        subCategory -> !subCategory.getIsDeleted())  // is_deleted가 false인 것만 필터링
                    .map(subCategory -> new CategoryDto(
                        subCategory.getCategoryId(),
                        subCategory.getName()
                    ))
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());
    }

    /**
     * 카테고리 상세 정보 조회
     *
     * @param id
     * @return
     */
    public CategoryDetailsDto getCategoryById(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findByCategoryIdAndIsDeletedFalse(
            id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            return new CategoryDetailsDto(
                category.getCategoryId(),
                category.getName(),
                category.getDescription(),
                category.getParentCategory() != null ?
                    new CategoryDto(category.getParentCategory().getCategoryId(),
                        category.getParentCategory().getName()) : null
            );
        } else {
            throw new IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다.");
        }
    }


}
