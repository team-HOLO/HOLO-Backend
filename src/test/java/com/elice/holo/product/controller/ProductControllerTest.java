package com.elice.holo.product.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.elice.holo.product.controller.dto.AddProductRequest;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    public void mockMvcSetUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build();
    }


    @DisplayName("상품 등록 테스트")
    @Test
    void saveProductTest() throws Exception {

        //given
        final String url = "/api/products";
        final String name = "의자";
        final int price = 100000;
        String description = "시디즈 의자";
        int stockQuantity = 999;

        AddProductRequest request = new AddProductRequest(name, price, description,
            stockQuantity);

        String requestBody = objectMapper.writeValueAsString(request); //json mapping

        //when
        ResultActions result = mockMvc.perform(
            post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        //then
        result
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.description").value(description));

    }





}