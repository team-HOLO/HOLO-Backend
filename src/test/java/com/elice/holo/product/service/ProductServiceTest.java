package com.elice.holo.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.repository.CategoryRepository;
import com.elice.holo.product.ProductMapper;
import com.elice.holo.product.dto.AddProductResponse;
import com.elice.holo.product.dto.ProductOptionDto;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.domain.ProductOption;
import com.elice.holo.product.dto.ProductResponseDto;
import com.elice.holo.product.dto.ProductSearchCond;
import com.elice.holo.product.dto.UpdateProductOptionDto;
import com.elice.holo.product.dto.UpdateProductRequest;
import com.elice.holo.product.exception.DuplicateProductNameException;
import com.elice.holo.product.exception.ProductNotFoundException;
import com.elice.holo.product.repository.ProductRepository;
import com.elice.holo.product.dto.AddProductRequest;
import com.elice.holo.product.dto.ProductsResponseDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductImageService productImageService;

    @Mock
    private LocalStorageService localStorageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("상품 등록 테스트")
    void saveProductTest() throws IOException {

        //given
        Product product = Product.createProduct("의자", 300000, "시디즈", 100);
        Category category = Category.builder()
            .name("가구")
            .description("전체 가구 카테고리")
            .parentCategory(null)
            .build();
        Category savedCategory = categoryRepository.save(category);

        boolean isThumbnail = false;
        AddProductRequest request = new AddProductRequest("의자", 300000, "시디즈", 100,
            getProductOptionDto(), List.of(isThumbnail));
        request.setCategoryId(1L);

        //mockMultipartFile
        MockMultipartFile mockFile = new MockMultipartFile("file", "test-image.jpg", "image/jpeg", "test image content".getBytes());
        List<MultipartFile> multipartFiles = List.of(mockFile);

        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(categoryRepository.findByCategoryIdAndIsDeletedFalse(1L)).thenReturn(Optional.of(category));

        //when
        AddProductResponse response = productService.saveProduct(request, multipartFiles);

        //then
        assertNotNull(response);
        assertThat(response.getDescription()).isEqualTo("시디즈");
        assertThat(response.getPrice()).isEqualTo(300000);
        assertThat(response.getName()).isEqualTo("의자");
    }

    @Test
    @DisplayName("상품 단일 조회 테스트")
    void getProductTest() {

        //given
        Product product = Product.createProduct("의자", 300000, "시디즈", 100);

        getProductOptionDto().stream()
            .map(ProductOptionDto::toEntity)
            .collect(Collectors.toList()).forEach(product::addProductOption);

        when(productRepository.findProductDetailByProductId(any(Long.class))).thenReturn(Optional.of(product));

        //when
        ProductResponseDto response = productService.findProductById(1L);

        //then
        assertThat(response.getName()).isEqualTo("의자");
        assertThat(response.getProductOptions().get(0).getSize()).isEqualTo("L");
        assertThat(response.getProductOptions())
            .extracting("color")
            .containsExactly("white", "black");
    }

    @Test
    @DisplayName("상품이 존재하지 않으면 NotFoundException 발생")
    void getProductExceptionTest() {

        //given
        Long id = 999L;
        when(productRepository.findProductDetailByProductId(id)).thenReturn(Optional.empty());

        //when
        ProductNotFoundException exception =
            assertThrows(ProductNotFoundException.class, () -> {
                productService.findProductById(id);
            });

        //then
        assertThat(exception.getMessage()).contains("상품이 존재하지 않습니다.");
        verify(productRepository, times(1)).findProductDetailByProductId(id);
    }

    @Test
    @DisplayName("상품 이름이 중복되면 DuplicateProductNameException 발생")
    void ValidateDuplicateProductNameTest() {

        //given
        Product product = Product.createProduct("트롤리", 100000, "모던하우스 트롤리", 100);
        when(productRepository.existsByNameAndIsDeletedFalse("트롤리")).thenReturn(true);

        AddProductRequest request = new AddProductRequest("트롤리", 100000, "중복 트롤리", 100,
            getProductOptionDto(), List.of(false));

        //when & then
        assertThrows(DuplicateProductNameException.class, () ->
            productService.saveProduct(request, List.of())
        );

        verify(productRepository, never()).save(any(Product.class));

    }

    @Test
    @DisplayName("상품 다수 조회 테스트")
    void getAllProductTest() {

        //given
        Product product1 = Product.createProduct("의자", 300000, "시디즈", 100);
        Product product2 = Product.createProduct("책상", 100000, "데스크", 200);
        List<Product> productList = Arrays.asList(product1, product2);
        ProductSearchCond cond = new ProductSearchCond();
        PageRequest pageRequest = PageRequest.of(0, 10);

        getProductOptionDto().stream()
            .map(ProductOptionDto::toEntity)
            .collect(Collectors.toList())
            .forEach(product2::addProductOption);

        List<ProductsResponseDto> result = productList.stream()
            .map(ProductsResponseDto::new)
            .collect(Collectors.toList());

        when(productRepository.findProductsPage(pageRequest, cond)).thenReturn(
            new PageImpl<>(result, pageRequest, result.size()));

        //when
        Page<ProductsResponseDto> productsPage = productService.findProducts(pageRequest, cond);
        List<ProductsResponseDto> products = productsPage.getContent();

        //then
        assertThat(products.get(1).getName()).isEqualTo("책상");
        assertThat(products.get(0).getPrice()).isEqualTo(300000);
    }

    @Test
    @DisplayName("상품 수정 테스트")
    void updateProductTest() {

        //given
        Long productId = 1L;
        Product product = Product.createProduct("침대", 777777, "시몬스 침대", 100);
        getProductOptionDto().stream()
            .map(ProductOptionDto::toEntity)
            .forEach(product::addProductOption);

        UpdateProductOptionDto updateDto = new UpdateProductOptionDto(null, "brown", "M", 30);
//        UpdateProductOptionDto existingOptionDto = new UpdateProductOptionDto(1L, "white", "L", 30);
        boolean isThumbnail = false;
        UpdateProductRequest updateRequest = new UpdateProductRequest(
            "침대 수정", 200000, "에이스 침대", 100, List.of(updateDto), List.of(isThumbnail));

        when(productRepository.findProductDetailByProductId(productId)).thenReturn(Optional.of(product));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        //when
        productService.updateProduct(productId, updateRequest);

        //then
        ProductResponseDto response = productService.findProductById(productId);
        Product updatedProduct = productRepository.findById(productId).get();
        assertThat(updatedProduct.getName()).isEqualTo("침대 수정");
        assertThat(updatedProduct.getDescription()).isEqualTo("에이스 침대");
        assertThat(updatedProduct.getProductOptions().size()).isEqualTo(3);
        assertThat(updatedProduct.getProductOptions().get(2).getColor()).isEqualTo("brown");
        assertThat(updatedProduct.getProductOptions().get(0).isDeleted()).isTrue();
        assertThat(updatedProduct.getProductOptions().get(1).isDeleted()).isTrue();
    }

    @Test
    @DisplayName("상품 삭제 테스트")
    public void softDeleteProductTest() throws Exception {

        //given
        Long productId = 1L;
        Product product = Product.createProduct("침대", 777777, "시몬스 침대", 100);

        when(productRepository.findByProductIdAndIsDeletedFalse(productId)).thenReturn(
            Optional.of(product));

        //when
        productService.deleteProduct(productId);

        //then
        Product deletedProduct = productRepository.findByProductIdAndIsDeletedFalse(productId).get();
        assertThat(deletedProduct.getIsDeleted()).isTrue();
    }

    @Test
    @DisplayName("삭제할 상품이 존재하지 않으면 NotFoundException 발생")
    void deleteProductNotFoundExceptionTest() {

        //given
        Long id = 999L;
        when(productRepository.findByProductIdAndIsDeletedFalse(id)).thenReturn(Optional.empty());

        //when
        ProductNotFoundException exception =
            assertThrows(ProductNotFoundException.class, () -> {
                productService.deleteProduct(id);
            });

        //then
        assertThat(exception.getMessage()).contains("삭제할 상품이 존재하지 않습니다.");
        verify(productRepository, times(1)).findByProductIdAndIsDeletedFalse(id);
    }



    //옵션 생성 메서드
    private List<ProductOptionDto> getProductOptionDto() {

        ProductOption option1 = ProductOption.createOption( "white", "L", 30);
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