package com.elice.holo.category.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryListDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import com.elice.holo.category.exception.CategoryNotFoundException;
import com.elice.holo.category.exception.DuplicateCategoryNameException;
import com.elice.holo.category.mapper.CategoryMapper;
import com.elice.holo.category.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Nested
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

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
        Category category = Category.builder()
            .categoryId(categoryId)
            .name("가구")
            .description("1인용 가구")
            .isDeleted(false)
            .build();

        CategoryCreateDto categoryCreateDto = new CategoryCreateDto("가구", "1인용 가구", null);
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto(categoryId, "가구",
            new ArrayList<>());

        when(categoryMapper.toEntity(categoryCreateDto)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toCategoryResponseDto(category)).thenReturn(categoryResponseDto);

        // when
        CategoryResponseDto savedCategory = categoryService.createCategory(categoryCreateDto);

        // then
        assertNotNull(savedCategory);
        assertEquals(categoryId, savedCategory.getCategoryId());
        assertEquals(category.getName(), savedCategory.getName());
    }

    @Test
    @DisplayName("카테고리 등록 테스트 - 중복된 이름으로 카테고리 등록시 예외 발생")
    void createCategoryDuplicateNameTest() {
        // given
        CategoryCreateDto categoryCreateDto = new CategoryCreateDto("가구", "1인용 가구", null);

        when(categoryRepository.existsByNameAndIsDeletedFalse(
            categoryCreateDto.getName())).thenReturn(true);

        // when & then
        assertThrows(DuplicateCategoryNameException.class,
            () -> categoryService.createCategory(categoryCreateDto));
    }

    @Test
    @DisplayName("카테고리 등록 테스트 - 존재하지 않는 부모 카테고리로 카테고리 등록 시 예외 발생")
    void createCategoryWithInvalidParentTest() {
        // given
        Long invalidParentCategoryId = 999L;
        CategoryCreateDto categoryCreateDto = new CategoryCreateDto("가구", "1인용 가구",
            invalidParentCategoryId);

        // 이름은 중복되지 않음
        when(categoryRepository.existsByNameAndIsDeletedFalse(
            categoryCreateDto.getName())).thenReturn(false);
        // 존재하지 않는 부모 카테고리
        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(
            invalidParentCategoryId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CategoryNotFoundException.class,
            () -> categoryService.createCategory(categoryCreateDto));
    }

    @Test
    @DisplayName("카테고리 업데이트 테스트")
    void updateCategoryTest() {
        // given
        Long categoryId = 1L;
        Long parentCategoryId = 2L;

        // 기존 카테고리
        Category category = Category.builder()
            .categoryId(categoryId)
            .name("카테고리1")
            .isDeleted(false)
            .build();

        // 상위 카테고리
        Category parentCategory = Category.builder()
            .categoryId(parentCategoryId)
            .name("상위 카테고리")
            .isDeleted(false)
            .build();

        CategoryCreateDto updateDto = new CategoryCreateDto("카테고리2", "카테고리 업데이트", parentCategoryId);
        CategoryResponseDto updatedResponseDto = new CategoryResponseDto(categoryId, "카테고리2",
            new ArrayList<>());

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(categoryId)).thenReturn(
            Optional.of(category));
        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(parentCategoryId)).thenReturn(
            Optional.of(parentCategory));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toCategoryResponseDto(category)).thenReturn(updatedResponseDto);

        // when
        CategoryResponseDto result = categoryService.updateCategory(categoryId, updateDto);

        // then
        assertNotNull(result);
        assertEquals("카테고리2", result.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("카테고리 업데이트 테스트 - 잘못된 ID로 카테고리 업데이트 시 예외 발생")
    void updateCategoryWithInvalidIdTest() {
        // given
        Long invalidCategoryId = 999L;
        CategoryCreateDto updateDto = new CategoryCreateDto("카테고리2", "카테고리 업데이트", null);

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(invalidCategoryId))
            .thenReturn(Optional.empty());

        // when & then
        assertThrows(CategoryNotFoundException.class,
            () -> categoryService.updateCategory(invalidCategoryId, updateDto));
    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    void deleteCategoryTest() {
        // given
        Long categoryId = 1L;
        Category category = Category.builder()
            .categoryId(categoryId)
            .name("카테고리")
            .description("설명")
            .isDeleted(false)
            .build();

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(categoryId)).thenReturn(
            Optional.of(category));

        // when
        categoryService.deleteCategory(categoryId);

        // then
        verify(categoryRepository, times(1)).save(category);
        assertEquals(true, category.getIsDeleted());
    }

    @Test
    @DisplayName("카테고리 삭제 테스트 - 존재하지 않는 카테고리 삭제 시 예외 발생")
    void deleteNonExistentCategoryTest() {
        // given
        Long nonExistentCategoryId = 999L;

        when(
            categoryRepository.findByCategoryIdAndIsDeletedFalse(nonExistentCategoryId)).thenReturn(
            Optional.empty());

        // when & then
        assertThrows(CategoryNotFoundException.class,
            () -> categoryService.deleteCategory(nonExistentCategoryId));
    }

    @Test
    @DisplayName("전체 카테고리 목록 조회 테스트")
    void findAllCategoriesTest() {
        // given
        Category category1 = Category.builder()
            .categoryId(1L)
            .name("category1")
            .subCategories(new ArrayList<>())
            .isDeleted(false)
            .build();

        Category category2 = Category.builder()
            .categoryId(2L)
            .name("category2")
            .subCategories(new ArrayList<>())
            .isDeleted(false)
            .build();

        List<Category> categories = Arrays.asList(category1, category2);
        CategoryResponseDto dto1 = new CategoryResponseDto(1L, "category1", new ArrayList<>());
        CategoryResponseDto dto2 = new CategoryResponseDto(2L, "category2", new ArrayList<>());

        when(categoryRepository.findByIsDeletedFalse()).thenReturn(categories);
        when(categoryMapper.toCategoryResponseDto(category1)).thenReturn(dto1);
        when(categoryMapper.toCategoryResponseDto(category2)).thenReturn(dto2);

        // when
        List<CategoryResponseDto> result = categoryService.getAllCategories();

        // then
        assertEquals(2, result.size());
        assertEquals("category1", result.getFirst().getName());
    }

    @Test
    @DisplayName("카테고리 상세정보 조회 테스트")
    public void testGetCategoryById() {
        // given
        Long categoryId = 1L;
        Category category = Category.builder()
            .categoryId(categoryId)
            .name("Category1")
            .description("Description")
            .isDeleted(false)
            .build();

        CategoryDetailsDto categoryDetailsDto = new CategoryDetailsDto(categoryId, "Category1",
            "Description", null);

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(categoryId)).thenReturn(
            Optional.of(category));
        when(categoryMapper.toCategoryDetailsDto(category)).thenReturn(categoryDetailsDto);

        // when
        CategoryDetailsDto result = categoryService.getCategoryById(categoryId);

        // then
        assertNotNull(result);
        assertEquals("Category1", result.getName());
    }

    @Test
    @DisplayName("카테고리 상세정보 조회 테스트 - 존재하지 않는 ID로 카테고리 상세정보 조회시 예외 발생")
    public void testGetCategoryByIdWithInvalidId() {
        // given
        Long invalidCategoryId = 999L;

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(invalidCategoryId)).thenReturn(
            Optional.empty());

        // when & then
        assertThrows(CategoryNotFoundException.class,
            () -> categoryService.getCategoryById(invalidCategoryId));
    }

    @Test
    @DisplayName("삭제된 카테고리 조회 시 예외 발생")
    public void testGetDeletedCategory() {
        // given
        Long categoryId = 1L;

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(categoryId)).thenReturn(
            Optional.empty());

        // when & then
        assertThrows(CategoryNotFoundException.class,
            () -> categoryService.getCategoryById(categoryId));
    }


    @Test
    @DisplayName("최상위 카테고리 조회 테스트")
    public void testGetTopLevelCategories() {
        // given
        Category topCategory = Category.builder()
            .categoryId(1L)
            .name("topCategory")
            .isDeleted(false)
            .build();

        CategoryResponseDto topCategoryDto = new CategoryResponseDto(1L, "topCategory",
            new ArrayList<>());

        List<Category> topCategories = Arrays.asList(topCategory);

        when(categoryRepository.findByIsDeletedFalseAndParentCategoryIsNull()).thenReturn(
            topCategories);
        when(categoryMapper.toCategoryResponseDto(topCategory)).thenReturn(topCategoryDto);

        // when
        List<CategoryResponseDto> result = categoryService.getTopLevelCategories();

        // then
        assertEquals(1, result.size());
        assertEquals("topCategory", result.getFirst().getName());
    }

    @Test
    @DisplayName("하위 카테고리 목록 조회 테스트")
    public void testGetSubCategories() {
        // given
        Long categoryId = 1L;
        Category parentCategory = Category.builder()
            .categoryId(categoryId)
            .isDeleted(false)
            .build();

        Category subCategory = Category.builder()
            .categoryId(2L)
            .name("subCategory")
            .description("SubCategory")
            .isDeleted(false)
            .build();

        List<Category> subCategories = Arrays.asList(subCategory);
        CategoryResponseDto subCategoryDto = new CategoryResponseDto(2L, "subCategory",
            new ArrayList<>());

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(categoryId)).thenReturn(
            Optional.of(parentCategory));
        when(categoryRepository.findByParentCategoryAndIsDeletedFalse(parentCategory)).thenReturn(
            subCategories);
        when(categoryMapper.toCategoryResponseDto(subCategory)).thenReturn(subCategoryDto);

        // when
        List<CategoryResponseDto> result = categoryService.getSubCategories(categoryId);

        // then
        assertEquals(1, result.size());
        assertEquals("subCategory", result.getFirst().getName());
    }

    @Test
    @DisplayName("페이지네이션 및 검색 기능 테스트 - 검색어 포함")
    public void testGetCategoriesPageableWithSearch() {
        // given
        String searchName = "Test";
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name"));
        Category category1 = Category.builder()
            .categoryId(1L)
            .name("Test Category 1")
            .isDeleted(false)
            .build();

        Category category2 = Category.builder()
            .categoryId(2L)
            .name("Another Category")
            .isDeleted(false)
            .build();

        Category category3 = Category.builder()
            .categoryId(3L)
            .name("Test Category 2")
            .isDeleted(false)
            .build();

        List<Category> categories = Arrays.asList(category1, category3);
        Page<Category> categoryPage = new PageImpl<>(categories);

        when(categoryRepository.findByIsDeletedFalseAndNameContainingIgnoreCase(eq(searchName),
            any(Pageable.class)))
            .thenReturn(categoryPage);
        when(categoryMapper.toCategoryListDto(category1))
            .thenReturn(new CategoryListDto(1L, null, "Test Category 1", "Description"));
        when(categoryMapper.toCategoryListDto(category3))
            .thenReturn(new CategoryListDto(3L, null, "Test Category 2", "Description"));

        // when
        Page<CategoryListDto> result = categoryService.getCategoriesPageable(0, 5, "name", "asc",
            searchName);

        // then
        assertEquals(2, result.getTotalElements());
        assertEquals("Test Category 1", result.getContent().get(0).getName());
        assertEquals("Test Category 2", result.getContent().get(1).getName());
    }

    @Test
    @DisplayName("페이지네이션 및 검색 기능 테스트 - 검색어 없음")
    public void testGetCategoriesPageableWithoutSearch() {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name"));
        Category category1 = Category.builder()
            .categoryId(1L)
            .name("Test Category 1")
            .isDeleted(false)
            .build();

        Category category2 = Category.builder()
            .categoryId(2L)
            .name("Test Category 2")
            .isDeleted(false)
            .build();

        List<Category> categories = Arrays.asList(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories);

        when(categoryRepository.findByIsDeletedFalse(any(Pageable.class)))
            .thenReturn(categoryPage);
        when(categoryMapper.toCategoryListDto(category1))
            .thenReturn(new CategoryListDto(1L, null, "Test Category 1", "Description"
            ));
        when(categoryMapper.toCategoryListDto(category2))
            .thenReturn(new CategoryListDto(2L, null, "Test Category 2", "Description"));

        // when
        Page<CategoryListDto> result = categoryService.getCategoriesPageable(0, 5, "name", "asc",
            null);

        // then
        assertEquals(2, result.getTotalElements());
        assertEquals("Test Category 1", result.getContent().get(0).getName());
        assertEquals("Test Category 2", result.getContent().get(1).getName());
    }

}
