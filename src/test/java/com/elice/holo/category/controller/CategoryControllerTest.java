package com.elice.holo.category.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        // 테스트 전에 필요한 데이터를 셋업합니다.
        categoryRepository.deleteAll(); // 테스트 간 데이터 초기화
    }

    @Test
    @DisplayName("전체 카테고리 목록 조회 통합 테스트")
    void testGetAllCategories() throws Exception {
        // Given
        Category category1 = Category.builder()
            .name("Category1")
            .description("Description1")
            .parentCategory(null)
            .build();
        Category category2 = Category.builder()
            .name("Category2")
            .description("Description2")
            .parentCategory(null)
            .build();
        categoryRepository.saveAll(List.of(category1, category2));

        // When & Then
        mockMvc.perform(get("/api/categories/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Category1"))
            .andExpect(jsonPath("$[1].name").value("Category2"));
    }

    @Test
    @DisplayName("새로운 카테고리 생성 통합 테스트")
    void testCreateCategory() throws Exception {
        // Given
        CategoryCreateDto createDto = new CategoryCreateDto("NewCategory", "NewDescription", null);

        // When & Then
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("NewCategory"));

        // Repository 검증
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(1);
        assertThat(categories.getFirst().getName()).isEqualTo("NewCategory");
    }

    @Test
    @DisplayName("카테고리 삭제 통합 테스트")
    void testDeleteCategory() throws Exception {
        // Given
        Category category = Category.builder()
            .name("CategoryToDelete")
            .description("Description")
            .parentCategory(null)
            .build();
        Category savedCategory = categoryRepository.save(category);

        // When & Then
        mockMvc.perform(delete("/api/categories/" + savedCategory.getCategoryId()))
            .andExpect(status().isNoContent());

        // Repository 검증
        assertThat(categoryRepository.findByCategoryIdAndIsDeletedFalse(
            savedCategory.getCategoryId())).isEmpty();
    }

    @Test
    @DisplayName("Admin 페이지에서 페이지네이션 및 검색 통합 테스트")
    void testGetCategoriesWithPaginationAndSearch() throws Exception {
        // Given
        Category category1 = Category.builder()
            .name("Category1")
            .description("Description1")
            .parentCategory(null)
            .build();
        Category category2 = Category.builder()
            .name("Category2")
            .description("Description2")
            .parentCategory(null)
            .build();
        Category category3 = Category.builder()
            .name("Another")
            .description("Description2")
            .parentCategory(null)
            .build();
        categoryRepository.saveAll(List.of(category1, category2, category3));

        // When & Then - 검색어가 없는 경우
        mockMvc.perform(get("/api/categories/admin")
                .param("page", "0")
                .param("size", "15")
                .param("sortBy", "name")
                .param("direction", "asc"))
            .andExpect(status().isOk())
            // 이름 순 정렬 확인
            .andExpect(jsonPath("$.content[0].name").value("Another"))
            .andExpect(jsonPath("$.content[1].name").value("Category1"))
            .andExpect(jsonPath("$.content[2].name").value("Category2"))
            .andExpect(jsonPath("$.content.length()").value(3));

        // When & Then - 검색어 "category" : 대,소문자 무시 테스트 포함
        mockMvc.perform(get("/api/categories/admin")
                .param("page", "0")
                .param("size", "15")
                .param("sortBy", "name")
                .param("direction", "asc")
                .param("name", "category"))
            .andExpect(status().isOk())
            // 이름에 category가 포함된 경우만 존재하는지 확인
            .andExpect(jsonPath("$.content[0].name").value("Category1"))
            .andExpect(jsonPath("$.content[1].name").value("Category2"))
            .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("상위 카테고리 목록 조회 통합 테스트")
    void testGetTopLevelCategories() throws Exception {
        // Given
        Category parentCategory = Category.builder()
            .name("ParentCategory")
            .description("Parent Description")
            .parentCategory(null)
            .build();
        Category childCategory = Category.builder()
            .name("ChildCategory")
            .description("Child Description")
            .parentCategory(parentCategory)
            .build();
        categoryRepository.save(parentCategory);
        categoryRepository.save(childCategory);

        // When & Then
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("ParentCategory"));
    }

    @Test
    @DisplayName("특정 카테고리의 하위 카테고리 목록 조회 통합 테스트")
    void testGetSubCategories() throws Exception {
        // Given
        Category parentCategory = Category.builder()
            .name("ParentCategory")
            .description("Parent Description")
            .parentCategory(null)
            .build();
        Category childCategory = Category.builder()
            .name("ChildCategory")
            .description("Child Description")
            .parentCategory(parentCategory)
            .build();
        Category savedParent = categoryRepository.save(parentCategory);
        categoryRepository.save(childCategory);

        // When & Then
        mockMvc.perform(get("/api/categories/sub/" + savedParent.getCategoryId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("ChildCategory"));
    }

    @Test
    @DisplayName("중복된 카테고리명으로 카테고리 생성 시도 통합 테스트")
    void testCreateCategoryWithDuplicateName() throws Exception {
        // Given
        Category existingCategory = Category.builder()
            .name("DuplicateCategory")
            .description("Description")
            .parentCategory(null)
            .build();
        categoryRepository.save(existingCategory);

        CategoryCreateDto createDto = new CategoryCreateDto("DuplicateCategory", "New Description",
            null);

        // When & Then
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isConflict());
    }
}