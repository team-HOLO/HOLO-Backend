package com.elice.holo.product.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.elice.holo.product.service.dto.AddProductRequest;
import com.elice.holo.product.service.dto.ProductOptionDto;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.domain.ProductOption;
import com.elice.holo.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

        AddProductRequest request = new AddProductRequest(name, price, description, stockQuantity,
            getProductOptionDto());

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

    @Test
    @DisplayName("상품 상세 조회 테스트")
    void getProductDetails() throws Exception{

        //given
        final String url = "/api/products/{id}";
        final String name = "책상";
        final int price = 200000;
        String description = "데스커 책상";
        int stockQuantity = 100;

        Product product = Product.createProduct(name, price, description, stockQuantity);
        getProductOptionDto().stream()
            .map(ProductOptionDto::toEntity)
            .collect(Collectors.toList()).forEach(product::addProductOption);

        Product savedProduct = productRepository.save(product);

        //when
        ResultActions resultActions = mockMvc.perform(get(url, savedProduct.getId()));

        //then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.productOptions[1].color").value("black"))
            .andExpect(jsonPath("$.productOptions[0].optionQuantity").value(30));

    }


    //옵션 생성 메서드
    private List<ProductOptionDto> getProductOptionDto() {

        ProductOption option1 = ProductOption.createOption("white", "L", 30);
        ProductOption option2 = ProductOption.createOption("black", "L", 30);

        List<ProductOption> optionList = new ArrayList<>();
        optionList.add(option1);
        optionList.add(option2);

        List<ProductOptionDto> productOptionDtoList = optionList.stream()
            .map(ol -> new ProductOptionDto(ol.getColor(), ol.getSize(), ol.getOptionQuantity()))
            .collect(Collectors.toList());

        return productOptionDtoList;
    }





}