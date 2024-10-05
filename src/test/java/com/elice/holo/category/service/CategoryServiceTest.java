package com.elice.holo.category.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import com.elice.holo.category.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("카테고리 등록 테스트")
    void createCategoryTest() {

        // given
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setName("가구");
        category.setDescription("1인용 가구");

        CategoryCreateDto categoryCreateDto = new CategoryCreateDto("가구", "1인용 가구", null);

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // when
        CategoryResponseDto savedCategory = categoryService.createCategory(categoryCreateDto);

        // then
        assertNotNull(savedCategory);
        assertEquals(categoryId, savedCategory.getCategoryId());
        assertEquals(category.getName(), savedCategory.getName());
    }

    @Test
    @DisplayName("존재하지 않는 부모 카테고리로 카테고리 등록 시 예외 발생")
    void createCategoryWithInvalidParentTest() {
        // given
        Long invalidParentCategoryId = 999L;
        CategoryCreateDto categoryCreateDto = new CategoryCreateDto("가구", "1인용 가구",
            invalidParentCategoryId);

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(invalidParentCategoryId))
            .thenReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchElementException.class, () -> {
            categoryService.createCategory(categoryCreateDto);
        });
    }

    @Test
    @DisplayName("카테고리 업데이트 테스트")
    void updateCategoryTest() {
        // given
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(1L);
        category.setName("카테고리1");

        CategoryCreateDto updateDto = new CategoryCreateDto("카테고리2",
            "카테고리 업데이트", null);

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(categoryId)).thenReturn(
            Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        // when
        CategoryResponseDto result = categoryService.updateCategory(categoryId, updateDto);

        // then
        assertEquals("카테고리2", result.getName());

    }

    @Test
    @DisplayName("잘못된 ID로 카테고리 업데이트 시 예외 발생")
    void updateCategoryWithInvalidIdTest() {
        // given
        Long invalidCategoryId = 999L;
        CategoryCreateDto updateDto = new CategoryCreateDto("카테고리2", "카테고리 업데이트", null);

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(invalidCategoryId))
            .thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.updateCategory(invalidCategoryId, updateDto);
        });
    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    void deleteCategoryTest() {
        // given
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(categoryId);

        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        // when
        categoryService.deleteCategory(categoryId);

        // then
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 삭제 시 예외 발생")
    void deleteNonExistentCategoryTest() {
        // given
        Long nonExistentCategoryId = 999L;

        when(categoryRepository.existsById(nonExistentCategoryId)).thenReturn(false);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.deleteCategory(nonExistentCategoryId);
        });
    }

    @Test
    @DisplayName("전체 카테고리 목록 조회")
    void findAllCategoriesTest() {
        // given
        Category category1 = new Category();
        category1.setCategoryId(1L);
        category1.setName("category1");
        category1.setSubCategories(new ArrayList<>());
        Category category2 = new Category();
        category2.setCategoryId(2L);
        category2.setName("category2");
        category2.setSubCategories(new ArrayList<>());

        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryRepository.findByIsDeletedFalse()).thenReturn(categories);

        // when
        List<CategoryResponseDto> result = categoryService.getAllCategories();

        // then
        assertEquals(2, result.size());
        assertEquals("category1", result.get(0).getName());
    }

    @Test
    @DisplayName("카테고리 상세정보 조회")
    public void testGetCategoryById() {
        // given
        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(categoryId);
        category.setName("Category1");
        category.setDescription("Description");

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(categoryId)).thenReturn(
            Optional.of(category));

        // when
        CategoryDetailsDto result = categoryService.getCategoryById(categoryId);

        // then
        assertEquals("Category1", result.getName());
    }

    @Test
    @DisplayName("잘못된 ID로 카테고리 상세정보 조회시 예외 발생")
    public void testGetCategoryByIdWithInvalidId() {
        // given
        Long invalidCategoryId = 999L;

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(invalidCategoryId))
            .thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.getCategoryById(invalidCategoryId);
        });
    }

    @Test
    @DisplayName("삭제된 카테고리 조회 시 예외 발생")
    public void testGetDeletedCategory() {
        // given
        Long categoryId = 1L;
        Category deletedCategory = new Category();
        deletedCategory.setCategoryId(categoryId);
        deletedCategory.setIsDeleted(true);  // 삭제된 상태로 설정

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(categoryId))
            .thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.getCategoryById(categoryId);
        });
    }

    @Test
    @DisplayName("최상위 카테고리 조회")
    public void testGetTopLevelCategories() {
        // given
        Category topCategory = new Category();
        topCategory.setCategoryId(1L);
        topCategory.setName("topCategory");
        List<Category> topCategories = Arrays.asList(topCategory);

        when(categoryRepository.findByIsDeletedFalseAndParentCategoryIsNull()).thenReturn(
            topCategories);

        // when
        List<CategoryResponseDto> result = categoryService.getTopLevelCategories();

        // then
        assertEquals(1, result.size());
        assertEquals("topCategory", result.get(0).getName());
    }

    @Test
    @DisplayName("하위 카테고리 목록 조회")
    public void testGetSubCategories() {
        // given
        Long categoryId = 1L;
        Category parentCategory = new Category();
        parentCategory.setCategoryId(categoryId);

        Category subCategory = new Category();
        subCategory.setCategoryId(2L);
        subCategory.setName("subCategory");
        subCategory.setDescription("SubCategory");

        List<Category> subCategories = Arrays.asList(subCategory);

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(categoryId)).thenReturn(
            Optional.of(parentCategory));
        when(categoryRepository.findByParentCategoryAndIsDeletedFalse(parentCategory)).thenReturn(
            subCategories);

        // when
        List<CategoryResponseDto> result = categoryService.getSubCategories(categoryId);

        // then
        assertEquals(1, result.size());
        assertEquals("subCategory", result.get(0).getName());
    }

}