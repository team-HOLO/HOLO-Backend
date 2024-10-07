package com.elice.holo.category.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import com.elice.holo.category.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    CategoryService categoryService;

    @BeforeEach
    public void mockMvcSetUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build();
    }

    @DisplayName("카테고리 생성 테스트")
    @Test
    void createCategoryTest() throws Exception {
        // Given
        final String url = "/api/categories";
        CategoryCreateDto createDto = new CategoryCreateDto("가구", "1인용 가구", null);
        CategoryResponseDto responseDto = new CategoryResponseDto(1L, "가구", null);

        Mockito.when(categoryService.createCategory(Mockito.any(CategoryCreateDto.class)))
            .thenReturn(responseDto);

        String requestBody = objectMapper.writeValueAsString(createDto);

        // When
        ResultActions result = mockMvc.perform(
            post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // Then
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("가구"));
    }

    @Test
    @DisplayName("카테고리 상세 조회 테스트")
    void getCategoryDetailsTest() throws Exception {
        // Given
        final String url = "/api/categories/details/{id}";
        CategoryDetailsDto categoryDetailsDto = new CategoryDetailsDto(1L, "가구", "1인용 가구", null);
        Mockito.when(categoryService.getCategoryById(Mockito.anyLong()))
            .thenReturn(categoryDetailsDto);

        // When
        ResultActions resultActions = mockMvc.perform(get(url, 1L));

        // Then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("가구"));
    }

    @Test
    @DisplayName("전체 카테고리 목록 조회 테스트")
    void getAllCategoriesTest() throws Exception {
        // Given
        final String url = "/api/categories/all";
        List<CategoryResponseDto> categoryList = Arrays.asList(
            new CategoryResponseDto(1L, "가구", null),
            new CategoryResponseDto(2L, "주방", null)
        );

        Mockito.when(categoryService.getAllCategories())
            .thenReturn(categoryList);

        // When
        ResultActions resultActions = mockMvc.perform(get(url));

        // Then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("가구"))
            .andExpect(jsonPath("$[1].name").value("주방"));
    }

    @Test
    @DisplayName("카테고리 수정 테스트")
    void updateCategoryTest() throws Exception {
        // Given
        final String url = "/api/categories/{id}";
        CategoryCreateDto updateDto = new CategoryCreateDto("인테리어", "인테리어 소품", null);
        CategoryResponseDto updatedDto = new CategoryResponseDto(1L, "인테리어", null);

        Mockito.when(
                categoryService.updateCategory(Mockito.anyLong(), Mockito.any(CategoryCreateDto.class)))
            .thenReturn(updatedDto);

        String requestBody = objectMapper.writeValueAsString(updateDto);

        // When
        ResultActions result = mockMvc.perform(
            put(url, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("인테리어"));
    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    void deleteCategoryTest() throws Exception {
        // Given
        final String url = "/api/categories/{id}";

        Mockito.doNothing().when(categoryService).deleteCategory(Mockito.anyLong());

        // When
        ResultActions resultActions = mockMvc.perform(delete(url, 1L));

        // Then
        resultActions.andExpect(status().isNoContent());
    }
}