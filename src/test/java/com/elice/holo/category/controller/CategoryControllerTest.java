package com.elice.holo.category.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.repository.CategoryRepository;
import com.elice.holo.order.service.DiscordWebhookService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
    "HOLO_S3_ACCESS_KEY=mock-access-key",
    "HOLO_S3_SECRET_KEY=mock-secret-key"
})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @MockBean
    private DiscordWebhookService discordWebhookService; // DiscordWebhookService Mock

    private Category parentCategory;
    private Category childCategory;
    private Category category1;
    private Category category2;

    private final static String BASE_URL = "/api/categories";

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll(); // 테스트 간 데이터 초기화

        // 공통으로 사용할 상위 카테고리와 하위 카테고리 설정
        parentCategory = Category.builder()
            .name("ParentCategory")
            .description("Parent Description")
            .parentCategory(null)
            .build();
        childCategory = Category.builder()
            .name("ChildCategory")
            .description("Child Description")
            .parentCategory(parentCategory)
            .build();

        // 카테고리 저장
        category1 = Category.builder()
            .name("Category1")
            .description("Description1")
            .parentCategory(null)
            .build();
        category2 = Category.builder()
            .name("Category2")
            .description("Description2")
            .parentCategory(null)
            .build();
        categoryRepository.saveAll(List.of(parentCategory, childCategory, category1, category2));
    }

    @Test
    @DisplayName("전체 카테고리 목록 조회 통합 테스트")
    void testGetAllCategories() throws Exception {
        mockMvc.perform(get(BASE_URL + "/all"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(4));  // 배열의 길이가 4인지 확인
    }

    @Test
    @DisplayName("상위 카테고리 목록 조회 통합 테스트")
    void testGetTopLevelCategories() throws Exception {
        // When & Then
        mockMvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("ParentCategory"));
    }

    @Test
    @DisplayName("특정 카테고리의 하위 카테고리 목록 조회 통합 테스트")
    void testGetSubCategories() throws Exception {
        // When & Then
        mockMvc.perform(get(BASE_URL + "/sub/" + parentCategory.getCategoryId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("ChildCategory"));
    }
}