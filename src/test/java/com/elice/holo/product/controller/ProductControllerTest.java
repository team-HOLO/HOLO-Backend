package com.elice.holo.product.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.elice.holo.product.ProductMapper;
import com.elice.holo.product.dto.AddProductRequest;
import com.elice.holo.product.dto.ProductOptionDto;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.domain.ProductOption;
import com.elice.holo.product.dto.UpdateProductOptionDto;
import com.elice.holo.product.dto.UpdateProductRequest;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
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
        List<Boolean> isThumbnails = List.of(Boolean.TRUE, Boolean.FALSE);

        AddProductRequest request = new AddProductRequest(name, price, description, stockQuantity,
            getProductOptionDto(), isThumbnails);

        //mock 이미지 파일
        List<MultipartFile> multipartFiles = List.of(
            new MockMultipartFile("Image1", "Image1.jpg", "image/jpeg", "test image1 content".getBytes()),
            new MockMultipartFile("Image2", "Image2.jpg", "image/jpeg", "test image2 content".getBytes()));

        MockMultipartFile requestPart = new MockMultipartFile("addProductRequest", "request.json",
            "application/json",
            objectMapper.writeValueAsBytes(request));

        //when
        ResultActions result = mockMvc.perform(
            multipart(url)
                .file("productImages", multipartFiles.get(0).getBytes())
                .file("productImages", multipartFiles.get(1).getBytes())
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
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
        ResultActions resultActions = mockMvc.perform(get(url, savedProduct.getProductId()));

        //then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.productOptions[1].color").value("black"))
            .andExpect(jsonPath("$.productOptions[0].optionQuantity").value(30));

    }

    @Test
    @DisplayName("상품 목록 조회 테스트")
    void getAllProductTest() throws Exception {

        //given
        final String url = "/api/products";
        Product product1 = Product.createProduct("의자", 300000, "시디즈", 100);
        Product product2 = Product.createProduct("책상", 100000, "데스크", 200);
        setProductOption(product1, product2);  //옵션 세팅

        productRepository.save(product1);
        productRepository.save(product2);

        //when
        ResultActions resultActions = mockMvc.perform(get(url));

        //then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].name").value("의자")) // content 배열의 첫 번째 요소
            .andExpect(jsonPath("$.content[1].price").value(100000)) // content 배열의 두 번째 요소
            .andExpect(jsonPath("$.totalElements").value(2)) // 총 요소 수
            .andExpect(jsonPath("$.totalPages").value(1)); // 총 페이지 수

    }



    @Test
    @DisplayName("상품 수정 테스트")
    void updateProductTest() throws Exception {

        //given
        final String url = "/api/products/{id}";
        Product product = Product.createProduct("의자", 300000, "시디즈", 100);
        getProductOptionDto().stream()
            .map(ProductOptionDto::toEntity)
            .forEach(product::addProductOption);

        Product savedProduct = productRepository.save(product);
        Long productOptionId = savedProduct.getProductOptions().get(1).getProductOptionId();

        UpdateProductOptionDto optionDto1 = new UpdateProductOptionDto(null, "brown", "F", 100);
        UpdateProductOptionDto optionDto2 = new UpdateProductOptionDto(productOptionId, "RED", "F", 700);
        boolean isThumbnail = false;
        UpdateProductRequest request = new UpdateProductRequest("의자(수정)", 200000, "시디즈(수정)",
            300, List.of(optionDto1, optionDto2), List.of(isThumbnail)
        );

        MockMultipartFile requestPart = new MockMultipartFile("updateProductRequest", "request.json",
            "application/json",
            objectMapper.writeValueAsBytes(request));

        //when
        ResultActions result = mockMvc.perform(
            multipart(url, savedProduct.getProductId())
                .file(requestPart)
                .with( r -> {
                    r.setMethod("PUT");
                    return r;
                    }
                )
                .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        //then
        result.andExpect(status().isOk());

        Product updatedProduct = productRepository.findById(savedProduct.getProductId()).get();
        assertThat(updatedProduct.getName()).isEqualTo("의자(수정)");
        assertThat(updatedProduct.getDescription()).isEqualTo("시디즈(수정)");
        assertThat(updatedProduct.getProductOptions()).extracting("color")
            .containsExactly("white", "RED", "brown");
        assertThat(updatedProduct.getProductOptions().get(0).isDeleted()).isTrue();
    }

    @Test
    @DisplayName("상품 삭제 테스트")
    public void deleteProductTest() throws Exception {

        //given
        final String url = "/api/products/{id}";
        Product product = Product.createProduct("선반", 200000, "선반선반", 100);
        getProductOptionDto().stream()
            .map(ProductOptionDto::toEntity)
            .forEach(product::addProductOption);

        Product savedProduct = productRepository.save(product);

        //when
        ResultActions resultActions = mockMvc.perform(delete(url, savedProduct.getProductId()));

        //then
        resultActions.andExpect(status().isNoContent());

        Product deletedProduct = productRepository.findById(savedProduct.getProductId()).get();
        assertThat(deletedProduct.getIsDeleted()).isTrue();

    }



    //옵션 생성 메서드
    private List<ProductOptionDto> getProductOptionDto() {

        ProductOption option1 = ProductOption.createOption("white", "L", 30);
        ProductOption option2 = ProductOption.createOption("black", "L", 30);

        List<ProductOption> optionList = new ArrayList<>();
        optionList.add(option1);
        optionList.add(option2);

        List<ProductOptionDto> productOptionDtoList = optionList.stream()
//            .map(ol -> new ProductOptionDto(ol.getColor(), ol.getSize(), ol.getOptionQuantity()))
            .map(ol -> new ProductOptionDto(ol))
            .collect(Collectors.toList());

        return productOptionDtoList;
    }

    //옵션 setting 메서드
    private void setProductOption(Product product1, Product product2) {
        getProductOptionDto().stream()
            .map(ProductOptionDto::toEntity)
            .collect(Collectors.toList()).forEach(product1::addProductOption);
        getProductOptionDto().stream()
            .map(ProductOptionDto::toEntity)
            .collect(Collectors.toList()).forEach(product2::addProductOption);
    }





}