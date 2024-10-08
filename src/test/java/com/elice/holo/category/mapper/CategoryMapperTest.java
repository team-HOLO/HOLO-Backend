package com.elice.holo.category.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoryMapperTest {

    private CategoryMapper categoryMapper;

    @BeforeEach
    void setUp() {
        categoryMapper = CategoryMapper.INSTANCE;
    }

    @Test
    @DisplayName("Entity에서 CategoryDto 매핑")
    void testToCategoryDto() {
        Category category = Category.builder()
            .name("가전")
            .description("가전 제품")
            .parentCategory(null)
            .build();

        CategoryDto dto = categoryMapper.toCategoryDto(category);

        assertEquals(category.getCategoryId(), dto.getCategoryId());
        assertEquals(category.getName(), dto.getName());
    }

    @Test
    @DisplayName("Entity에서 CategoryResponseDto 매핑")
    void testToCategoryResponseDto() {
        Category subCategory1 = Category.builder()
            .categoryId(2L)
            .name("Mobile Phones")
            .subCategories(null)
            .isDeleted(false)
            .build();

        Category subCategory2 = Category.builder()
            .categoryId(3L)
            .name("Laptops")
            .subCategories(null)
            .isDeleted(false)
            .build();

        List<Category> subCategories = List.of(subCategory1, subCategory2);

        Category category = Category.builder()
            .categoryId(1L)
            .name("Electronics")
            .subCategories(subCategories)
            .isDeleted(false)
            .build();

        CategoryResponseDto dto = categoryMapper.toCategoryResponseDto(category);

        assertEquals(category.getCategoryId(), dto.getCategoryId());
        assertEquals(category.getName(), dto.getName());
        assertEquals(category.getSubCategories().size(), dto.getSubCategories().size());
    }

    @Test
    @DisplayName("CategoryCreateDto에서 Entity 매핑")
    void testToEntity() {
        CategoryCreateDto createDto = new CategoryCreateDto("Home Appliances",
            "Appliances for home", null);

        Category category = categoryMapper.toEntity(createDto);

        assertEquals(createDto.getName(), category.getName());
        assertEquals(createDto.getDescription(), category.getDescription());
        assertNull(category.getParentCategory());
    }

    @Test
    @DisplayName("Entity에서 CategoryDetailsDto 매핑")
    void testToCategoryDetailsDto() {
        Category parentCategory = Category.builder()
            .name("가전 제품").description("가전 제품 모음").parentCategory(null).build();
        Category category = Category.builder().name("모니터").description("모니터")
            .parentCategory(parentCategory).build();

        CategoryDetailsDto dto = categoryMapper.toCategoryDetailsDto(category);

        assertEquals(category.getCategoryId(), dto.getCategoryId());
        assertEquals(category.getName(), dto.getName());
        assertEquals(category.getDescription(), dto.getDescription());
        assertEquals(parentCategory.getCategoryId(), dto.getParentCategory().getCategoryId());
        assertEquals(parentCategory.getName(), dto.getParentCategory().getName());
    }

    @Test
    @DisplayName("Entity 목록을 CategoryDto List로 매핑")
    void testToCategoryDtoList() {
        Category category1 = Category.builder()
            .name("가전 제품").description("가전 제품 모음").parentCategory(null).build();
        Category category2 = Category.builder()
            .name("가구").description("가구 모음").parentCategory(null).build();
        List<Category> categories = List.of(category1, category2);

        List<CategoryDto> dtoList = categoryMapper.toCategoryDtoList(categories);

        assertEquals(categories.size(), dtoList.size());
        assertEquals(categories.get(0).getCategoryId(), dtoList.get(0).getCategoryId());
        assertEquals(categories.get(1).getCategoryId(), dtoList.get(1).getCategoryId());
    }
}