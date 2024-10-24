package com.elice.holo.product.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.repository.CategoryRepository;
import com.elice.holo.member.domain.Member;
import com.elice.holo.member.domain.MemberDetails;
import com.elice.holo.order.service.DiscordWebhookService;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.domain.ProductOption;
import com.elice.holo.product.dto.AddProductRequest;
import com.elice.holo.product.dto.ProductOptionDto;
import com.elice.holo.product.dto.UpdateProductOptionDto;
import com.elice.holo.product.dto.UpdateProductRequest;
import com.elice.holo.product.repository.ProductRepository;
import com.elice.holo.product.service.ProductService;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ProductAdminControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private ProductService productService;

    @MockBean
    private DiscordWebhookService discordWebhookService; // DiscordWebhookService Mock

    @BeforeEach
    public void mockMvcSetUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build();

        Member member = Member.builder()
            .memberId(1L)
            .email("admin@example.com")
            .password("password")
            .name("Admin")
            .isAdmin(true)  //관리자 권한 설정
            .isDeleted(false)
            .tel("010-1234-5678")

            .gender(true)
            .build();

        MemberDetails memberDetails = new MemberDetails(member);

        SecurityContextHolder.setContext(new SecurityContextImpl(
            new UsernamePasswordAuthenticationToken(memberDetails, "password",
                memberDetails.getAuthorities())));
    }

    private final String url = "/api/admin/products";

    @DisplayName("상품 등록 테스트")
    @Test
    void saveProductTest() throws Exception {

        //given
        final String name = "의자";
        final int price = 100000;
        String description = "시디즈 의자";
        int stockQuantity = 999;
        List<Boolean> isThumbnails = List.of(Boolean.TRUE, Boolean.FALSE);

        Category category1 = Category.builder()
            .name("가구")
            .description("전체 가구 카테고리")
            .parentCategory(null)
            .build();
        categoryRepository.save(category1);

        AddProductRequest request = new AddProductRequest(name, price, description, stockQuantity,
            getProductOptionDto(), isThumbnails);
        request.setCategoryId(category1.getCategoryId());

        //mock 이미지 파일
        List<MockMultipartFile> multipartFiles = List.of(
            new MockMultipartFile("productImages", "Image1.jpg", "image/jpeg", "test image1 content".getBytes()),
            new MockMultipartFile("productImages", "Image2.jpg", "image/jpeg", "test image2 content".getBytes()));

        MockMultipartFile requestPart = new MockMultipartFile("addProductRequest", "request.json",
            "application/json",
            objectMapper.writeValueAsBytes(request));

        //when
        ResultActions result = mockMvc.perform(
            multipart(url)
                .file(multipartFiles.get(0))
                .file(multipartFiles.get(1))
                .file(requestPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        );

        //then
        result
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.description").value(description));
    }

    @Test
    @DisplayName("상품 이름이 중복되면 409 conflict 상태 코드 반환 ")
    void validateDuplicateProductNameTest() throws Exception {

        //given
        Category category1 = Category.builder()
            .name("가구")
            .description("전체 가구 카테고리")
            .parentCategory(null)
            .build();
        categoryRepository.save(category1);

        //첫번째 상품 등록
        AddProductRequest request = new AddProductRequest("트롤리", 100000, "트롤리", 100,
            getProductOptionDto(), List.of(Boolean.FALSE));
        request.setCategoryId(category1.getCategoryId());

        MockMultipartFile requestPart = new MockMultipartFile("addProductRequest", "request.json",
            "application/json", objectMapper.writeValueAsBytes(request));

        MockMultipartFile image = new MockMultipartFile("productImages", "Image1.jpg", "image/jpeg",
            "test image1 content".getBytes());

        mockMvc.perform(multipart(url)
            .file(image)
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA));

        //두번째 상품 등록
        AddProductRequest request2 = new AddProductRequest("트롤리", 100000, "중복 트롤리", 100,
            getProductOptionDto(), List.of(Boolean.FALSE));
        request2.setCategoryId(category1.getCategoryId());

        MockMultipartFile requestPart2 = new MockMultipartFile("addProductRequest", "request.json",
            "application/json", objectMapper.writeValueAsBytes(request2));

        MockMultipartFile image2 = new MockMultipartFile("productImages", "Image1.jpg", "image/jpeg",
            "test image1 content".getBytes());

        //when
        ResultActions result = mockMvc.perform(multipart(url)
            .file(image2)
            .file(requestPart2)
            .contentType(MediaType.MULTIPART_FORM_DATA));

        //then
        result.andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Duplicate product name"));
    }

    @Test
    @DisplayName("상품 이미지 등록시 이미지 파일이 아닐 경우 415 unsupported media type 상태 코드 반환")
    public void InvalidFileExtensionTest() throws Exception {

        //given
        Category category1 = Category.builder()
            .name("가구")
            .description("전체 가구 카테고리")
            .parentCategory(null)
            .build();
        categoryRepository.save(category1);

        //첫번째 상품 등록
        AddProductRequest request = new AddProductRequest("침대", 100000, "에이스 침대", 100,
            getProductOptionDto(), List.of(Boolean.FALSE));
        request.setCategoryId(category1.getCategoryId());

        MockMultipartFile requestPart = new MockMultipartFile("addProductRequest", "request.json",
            "application/json", objectMapper.writeValueAsBytes(request));

        MockMultipartFile invalidFile = new MockMultipartFile("productImages", "file.txt", "image/jpeg",
            "test image1 content".getBytes());

        //when
        ResultActions result = mockMvc.perform(multipart(url)
            .file(invalidFile)
            .file(requestPart)
            .contentType(MediaType.MULTIPART_FORM_DATA));

        //then
        result.andExpect(status().isUnsupportedMediaType())
            .andExpect(jsonPath("$.message").value("Invalid File Extension"))
            .andExpect(jsonPath("$.status").value(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()));
    }

    @Test
    @DisplayName("상품 수정 테스트")
    void updateProductTest() throws Exception {

        //given
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
            300, null, List.of(optionDto1, optionDto2), List.of(isThumbnail)
        );

        MockMultipartFile requestPart = new MockMultipartFile("updateProductRequest", "request.json",
            "application/json",
            objectMapper.writeValueAsBytes(request));

        //when
        ResultActions result = mockMvc.perform(
            multipart(url + "/{id}", savedProduct.getProductId())
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
        Product product = Product.createProduct("선반", 200000, "선반선반", 100);
        getProductOptionDto().stream()
            .map(ProductOptionDto::toEntity)
            .forEach(product::addProductOption);

        Product savedProduct = productRepository.save(product);

        //when
        ResultActions resultActions = mockMvc.perform(delete(url + "/{id}", savedProduct.getProductId()));

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
}
