package com.elice.holo.category.service;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import com.elice.holo.category.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
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
    @Transactional
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
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryCreateDto updateDto) {
        Optional<Category> optionalCategory = categoryRepository.findByCategoryIdAndIsDeletedFalse(
            id);

        if (optionalCategory.isPresent()) {
            Category targetCategory = optionalCategory.get();
            targetCategory.setName(updateDto.getName());
            targetCategory.setDescription(updateDto.getDescription());

            // ParentCategory가 null인지 체크 후, null이 아니면 설정
            if (updateDto.getParentCategory() != null) {
                Category parentCategory = categoryRepository.findByCategoryIdAndIsDeletedFalse(
                        updateDto.getParentCategory())
                    .orElseThrow(() -> new IllegalArgumentException("해당 Parent 카테고리가 존재하지 않습니다."));
                targetCategory.setParentCategory(parentCategory);
            } else {
                targetCategory.setParentCategory(null); // 혹은 ParentCategory를 null로 설정
            }

            Category updatedCategory = categoryRepository.save(targetCategory);

            return new CategoryResponseDto(updatedCategory.getCategoryId(),
                updatedCategory.getName(), null);
        } else {
            throw new IllegalArgumentException("해당 ID의 카테고리가 존재하지 않습니다.");
        }
    }

    /**
     * 주어진 ID에 해당하는 카테고리를 삭제하는 메서드 카테고리가 존재하지 않을 경우 IllegalArgumentException 예외 발생
     *
     * @param id 삭제할 카테고리의 ID
     * @throws IllegalArgumentException 해당 ID의 카테고리가 존재하지 않을 경우 발생
     */
    @Transactional
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
     * @return 전체 카체고리의 CategoryResponseDto 리스트
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
     * @param id 상세 조회할 카테고리의 ID
     * @return 조회 대상의 카테고리 정보를 담은 CategoryDetailsDto
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


    /**
     * 최상위 카테고리(ParentCategory가 Null인) 목록을 반환하는 메서드
     *
     * @return 최상위 카테고리의 정보를 담은 CategoryResponseDto 목록
     */
    public List<CategoryResponseDto> getTopLevelCategories() {
        List<Category> topLevelCategories = categoryRepository.findByIsDeletedFalseAndParentCategoryIsNull();
        return topLevelCategories.stream()
            .map(category -> new CategoryResponseDto(
                category.getCategoryId(),
                category.getName(),
                Optional.ofNullable(category.getSubCategories()) // null 체크
                    .orElse(new ArrayList<>()) // null이면 빈 리스트 반환
                    .stream()
                    .filter(subCategory -> !subCategory.getIsDeleted())
                    .map(subCategory -> new CategoryDto(subCategory.getCategoryId(),
                        subCategory.getName()))
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList());
    }

    /**
     * 타겟 카테고리의 하위 카테고리 목록을 반환하는 메서드
     *
     * @param id 타켓 카테고리의 category id
     * @return 하위 카테고리의 정보가 담긴 CategoryResponseDto 목록
     */
    public List<CategoryResponseDto> getSubCategories(Long id) {
        Optional<Category> parentCategory = categoryRepository.findByCategoryIdAndIsDeletedFalse(
            id);
        if (parentCategory.isPresent()) {
            List<Category> subCategories = categoryRepository.findByParentCategoryAndIsDeletedFalse(
                parentCategory.get());
            return subCategories.stream()
                .map(subCategory -> new CategoryResponseDto(
                    subCategory.getCategoryId(),
                    subCategory.getName(),
                    null  // 하위 카테고리의 하위 카테고리는 존재하지 않음(현재 기준)
                ))
                .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("해당 ID의 대분류 카테고리가 존재하지 않습니다.");
        }
    }
}
