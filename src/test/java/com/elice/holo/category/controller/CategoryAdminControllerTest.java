package com.elice.holo.category.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import com.elice.holo.category.service.CategoryService;
import com.elice.holo.member.domain.Member;
import com.elice.holo.member.domain.MemberDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDetailsDto categoryDetailsDto;
    private CategoryCreateDto categoryCreateDto;
    private CategoryResponseDto categoryResponseDto;

    private final static String BASE_URL = "/api/admin/categories";

    @BeforeEach
    void setup() {
        // Create a Member entity for testing
        Member member = Member.builder()
            .memberId(1L)
            .email("admin@example.com")
            .password("password")
            .name("Admin")
            .isAdmin(true)
            .isDeleted(false)
            .tel("010-1234-5678")
            .age(30)
            .gender(true)
            .build();

        // Wrap the member with MemberDetails
        MemberDetails memberDetails = new MemberDetails(member);

        // Set the custom MemberDetails in the SecurityContext
        SecurityContextHolder.setContext(new SecurityContextImpl(
            new UsernamePasswordAuthenticationToken(memberDetails, "password",
                memberDetails.getAuthorities())
        ));

        // Additional setup code for DTOs
        categoryDetailsDto = new CategoryDetailsDto(1L, "Category 1", "Description", null);
        categoryCreateDto = new CategoryCreateDto("New Category", "New Description", null);
        categoryResponseDto = new CategoryResponseDto(1L, "New Category", new ArrayList<>());
    }

    @Test
    @DisplayName("Admin 권한으로 카테고리 상세 조회")
    void getCategoryDetails_withAdminRole_shouldReturnCategoryDetails() throws Exception {
        // Given
        Mockito.when(categoryService.getCategoryById(anyLong())).thenReturn(categoryDetailsDto);

        // When & Then
        mockMvc.perform(get(BASE_URL + "/details/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Category 1"))
            .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    @DisplayName("Admin 권한으로 카테고리 생성")
    void createCategory_withAdminRole_shouldReturnCreatedCategory() throws Exception {
        // Given
        Mockito.when(categoryService.createCategory(any())).thenReturn(categoryResponseDto);

        // When & Then
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryCreateDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("New Category"));
    }

    @Test
    @DisplayName("ADMIN이 아닌 사용자는 카테고리 생성 권한 없음")
    void createCategory_withUserRole_shouldReturnAccessDenied() throws Exception {
        // Given: 일반 유저 설정
        Member member = Member.builder()
            .memberId(2L)
            .email("user@example.com")
            .password("password")
            .name("User")
            .isAdmin(false)  // 일반 유저 권한 설정
            .isDeleted(false)
            .tel("010-1234-5678")
            .age(25)
            .gender(true)
            .build();

        // 일반 유저 권한을 가지는 MemberDetails 생성
        MemberDetails memberDetails = new MemberDetails(member);

        // SecurityContext에 일반 유저 설정
        SecurityContextHolder.setContext(new SecurityContextImpl(
            new UsernamePasswordAuthenticationToken(memberDetails, "password",
                memberDetails.getAuthorities())
        ));

        // When & Then
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryCreateDto)))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin 권한으로 카테고리 삭제")
    void deleteCategory_withAdminRole_shouldReturnNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete(BASE_URL + "/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Admin 권한으로 카테고리 목록 조회 (페이지네이션 적용)")
    void getCategories_withAdminRole_shouldReturnCategoryPage() throws Exception {
        // Given
        // 페이지네이션 및 카테고리 목록을 반환하는 로직을 목킹한다.
        Mockito.when(
                categoryService.getCategoriesPageable(anyInt(), anyInt(), anyString(), anyString(),
                    anyString()))
            .thenReturn(Page.empty());

        // When & Then
        mockMvc.perform(get(BASE_URL + "?page=0&size=15&sortBy=name&direction=asc"))
            .andExpect(status().isOk());
    }
}