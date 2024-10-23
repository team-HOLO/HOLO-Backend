package com.elice.holo.category.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    // 공통적으로 사용하는 Category 엔티티 생성 메서드
    private Category createCategory(Long id, String name, String description) {
        return Category.builder()
            .categoryId(id)
            .name(name)
            .description(description)
            .isDeleted(false)
            .build();
    }

    // 공통적으로 사용하는 DTO 생성 메서드
    private CategoryCreateDto createCategoryCreateDto(String name, String description,
        Long parentId) {
        return new CategoryCreateDto(name, description, parentId);
    }

    @Test
    @DisplayName("카테고리 등록 테스트")
    void createCategoryTest() {
        // given
        Category category = createCategory(1L, "가구", "1인용 가구");
        CategoryCreateDto categoryCreateDto = createCategoryCreateDto("가구", "1인용 가구", null);
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto(1L, "가구",
            new ArrayList<>());

        // mocking
        when(categoryMapper.toEntity(categoryCreateDto)).thenReturn(category);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toCategoryResponseDto(category)).thenReturn(categoryResponseDto);

        // when
        CategoryResponseDto savedCategory = categoryService.createCategory(categoryCreateDto);

        // then
        assertNotNull(savedCategory);
        assertEquals(1L, savedCategory.getCategoryId());
        assertEquals("가구", savedCategory.getName());
    }

    @Test
    @DisplayName("카테고리 등록 실패 테스트 - 중복된 이름")
    void createCategoryDuplicateNameTest() {
        // given
        CategoryCreateDto categoryCreateDto = createCategoryCreateDto("가구", "1인용 가구", null);

        // mocking
        when(categoryRepository.existsByNameAndIsDeletedFalse(
            categoryCreateDto.getName())).thenReturn(true);

        // when & then
        assertThrows(DuplicateCategoryNameException.class,
            () -> categoryService.createCategory(categoryCreateDto));
    }

    @Test
    @DisplayName("카테고리 업데이트 테스트")
    void updateCategoryTest() {
        // given
        Category category = createCategory(1L, "카테고리1", "설명");
        Category parentCategory = createCategory(2L, "상위 카테고리", "상위 설명");
        CategoryCreateDto updateDto = createCategoryCreateDto("카테고리2", "카테고리 업데이트", 2L);
        CategoryResponseDto updatedResponseDto = new CategoryResponseDto(1L, "카테고리2",
            new ArrayList<>());

        // mocking
        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(1L)).thenReturn(
            Optional.of(category));
        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(2L)).thenReturn(
            Optional.of(parentCategory));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toCategoryResponseDto(category)).thenReturn(updatedResponseDto);

        // when
        CategoryResponseDto result = categoryService.updateCategory(1L, updateDto);

        // then
        assertNotNull(result);
        assertEquals("카테고리2", result.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("부모 카테고리 삭제 테스트 - 하위 카테고리도 삭제")
    void deleteCategoryWithSubCategoriesTest() {
        // given
        Long parentCategoryId = 1L;
        Long childCategoryId = 2L;

        Category parentCategory = createCategory(parentCategoryId, "부모 카테고리", "설명");

        Category childCategory = createCategory(childCategoryId, "자식 카테고리", "설명");
        childCategory.updateParentCategory(parentCategory); // 자식 카테고리가 부모 카테고리를 참조

        List<Category> subCategories = Arrays.asList(childCategory);

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(parentCategoryId)).thenReturn(
            Optional.of(parentCategory));
        when(categoryRepository.findByParentCategoryAndIsDeletedFalse(parentCategory)).thenReturn(
            subCategories);

        // when
        categoryService.deleteCategory(parentCategoryId);

        // then
        assertTrue(parentCategory.getIsDeleted());
        assertTrue(childCategory.getIsDeleted());
        verify(categoryRepository, times(1)).save(parentCategory);
        verify(categoryRepository, times(1)).save(childCategory);
    }

    @Test
    @DisplayName("자식 카테고리 삭제 테스트 - 부모 카테고리는 삭제되지 않음")
    void deleteOnlyChildCategoryTest() {
        // given
        Long parentCategoryId = 1L;
        Long childCategoryId = 2L;

        Category parentCategory = createCategory(parentCategoryId, "부모 카테고리", "설명");

        Category childCategory = createCategory(childCategoryId, "자식 카테고리", "설명");
        childCategory.updateParentCategory(parentCategory); // 자식 카테고리가 부모 카테고리를 참조

        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(childCategoryId)).thenReturn(
            Optional.of(childCategory));

        // when
        categoryService.deleteCategory(childCategoryId);

        // then
        assertTrue(childCategory.getIsDeleted());
        assertFalse(parentCategory.getIsDeleted()); // 부모 카테고리는 삭제되지 않음
        verify(categoryRepository, times(1)).save(childCategory);
        verify(categoryRepository, times(0)).save(parentCategory); // 부모 카테고리는 저장되지 않음
    }

    @Test
    @DisplayName("전체 카테고리 목록 조회 테스트")
    void findAllCategoriesTest() {
        // given
        Category category1 = createCategory(1L, "category1", "설명1");
        Category category2 = createCategory(2L, "category2", "설명2");
        List<Category> categories = Arrays.asList(category1, category2);
        CategoryResponseDto dto1 = new CategoryResponseDto(1L, "category1", new ArrayList<>());
        CategoryResponseDto dto2 = new CategoryResponseDto(2L, "category2", new ArrayList<>());

        // mocking
        when(categoryRepository.findByIsDeletedFalse()).thenReturn(categories);
        when(categoryMapper.toCategoryResponseDto(category1)).thenReturn(dto1);
        when(categoryMapper.toCategoryResponseDto(category2)).thenReturn(dto2);

        // when
        List<CategoryResponseDto> result = categoryService.getAllCategories();

        // then
        assertEquals(2, result.size());
        assertEquals("category1", result.get(0).getName());
        assertEquals("category2", result.get(1).getName());
    }

    @Test
    @DisplayName("카테고리 상세정보 조회 테스트")
    public void testGetCategoryById() {
        // given
        Long categoryId = 1L;
        Category category = createCategory(categoryId, "Category1", "Description");

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
        Category topCategory = createCategory(1L, "topCategory", "description");

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
        Category parentCategory = createCategory(categoryId, "상위 카테고리", "설명1");

        Category subCategory = createCategory(2L, "하위 카테고리", "설명2");

        List<Category> subCategories = Arrays.asList(subCategory);
        CategoryResponseDto subCategoryDto = new CategoryResponseDto(2L, "하위 카테고리",
            new ArrayList<>());

        // mocking
        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(categoryId)).thenReturn(
            Optional.of(parentCategory));
        when(categoryRepository.findByParentCategoryAndIsDeletedFalse(parentCategory)).thenReturn(
            subCategories);
        when(categoryMapper.toCategoryResponseDto(subCategory)).thenReturn(subCategoryDto);

        // when
        List<CategoryResponseDto> result = categoryService.getSubCategories(categoryId);

        // then
        assertEquals(1, result.size());
        assertEquals("하위 카테고리", result.getFirst().getName());
    }


    @Test
    @DisplayName("페이지네이션 및 검색 기능 테스트 - 검색어 포함")
    public void testGetCategoriesPageableWithSearch() {
        // given
        String searchName = "Test";
        Pageable pageable = PageRequest.of(0, 5, Sort.by("name"));
        Category category1 = createCategory(1L, "Test Category 1", "설명");
        Category category3 = createCategory(3L, "Test Category 2", "설명");
        List<Category> categories = Arrays.asList(category1, category3);
        Page<Category> categoryPage = new PageImpl<>(categories);

        // mocking
        when(categoryRepository.findByIsDeletedFalseAndNameContainingIgnoreCase(eq(searchName),
            any(Pageable.class)))
            .thenReturn(categoryPage);
        when(categoryMapper.toCategoryListDto(category1)).thenReturn(
            new CategoryListDto(1L, null, "Test Category 1", "설명"));
        when(categoryMapper.toCategoryListDto(category3)).thenReturn(
            new CategoryListDto(3L, null, "Test Category 2", "설명"));

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
        Category category1 = createCategory(1L, "Test Category 1", "설명");
        Category category2 = createCategory(2L, "Test Category 2", "설명");
        List<Category> categories = Arrays.asList(category1, category2);
        Page<Category> categoryPage = new PageImpl<>(categories);

        // mocking
        when(categoryRepository.findByIsDeletedFalse(any(Pageable.class))).thenReturn(categoryPage);
        when(categoryMapper.toCategoryListDto(category1)).thenReturn(
            new CategoryListDto(1L, null, "Test Category 1", "설명"));
        when(categoryMapper.toCategoryListDto(category2)).thenReturn(
            new CategoryListDto(2L, null, "Test Category 2", "설명"));

        // when
        Page<CategoryListDto> result = categoryService.getCategoriesPageable(0, 5, "name", "asc",
            null);

        // then
        assertEquals(2, result.getTotalElements());
        assertEquals("Test Category 1", result.getContent().get(0).getName());
        assertEquals("Test Category 2", result.getContent().get(1).getName());
    }
}