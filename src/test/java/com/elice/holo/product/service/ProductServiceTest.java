package com.elice.holo.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.elice.holo.product.dto.ProductOptionDto;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.domain.ProductOption;
import com.elice.holo.product.dto.UpdateProductOptionDto;
import com.elice.holo.product.dto.UpdateProductRequest;
import com.elice.holo.product.exception.ProductNotFoundException;
import com.elice.holo.product.repository.ProductRepository;
import com.elice.holo.product.dto.AddProductRequest;
import com.elice.holo.product.dto.ProductsResponseDto;
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

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("상품 등록 테스트")
    void saveProductTest() {

        //given
        Product product = Product.createProduct("의자", 300000, "시디즈", 100);
        AddProductRequest request = new AddProductRequest("의자", 300000, "시디즈", 100,
            getProductOptionDto());
        when(productRepository.save(any(Product.class))).thenReturn(product);

        //when
        Product savedProduct = productService.saveProduct(request);

        //then
        assertNotNull(savedProduct);
        assertThat(savedProduct.getDescription()).isEqualTo("시디즈");
        assertThat(savedProduct.getPrice()).isEqualTo(300000);
        assertThat(savedProduct.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("상품 단일 조회 테스트")
    void getProductTest() {

        //given
        Product product = Product.createProduct("의자", 300000, "시디즈", 100);

        getProductOptionDto().stream()
            .map(ProductOptionDto::toEntity)
            .collect(Collectors.toList()).forEach(product::addProductOption);

        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

        //when
        Product findProduct = productService.findProductById(1L);

        //then
        assertThat(findProduct.getName()).isEqualTo("의자");
        assertThat(findProduct.getStockQuantity()).isEqualTo(100);
        assertThat(findProduct.getProductOptions().get(0).getSize()).isEqualTo("L");
        assertThat(findProduct.getProductOptions())
            .extracting("color")
            .containsExactly("white", "black");
    }

    @Test
    @DisplayName("상품이 존재하지 않으면 NotFoundException 발생")
    void getProductExceptionTest() {

        //given
        Long id = 999L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        //when
        ProductNotFoundException exception =
            assertThrows(ProductNotFoundException.class, () -> {
                productService.findProductById(id);
            });

        //then
        assertThat(exception.getMessage()).contains("상품이 존재하지 않습니다.");
        verify(productRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("상품 다수 조회 테스트")
    void getAllProductTest() {

        //given
        Product product1 = Product.createProduct("의자", 300000, "시디즈", 100);
        Product product2 = Product.createProduct("책상", 100000, "데스크", 200);
        List<Product> productList = Arrays.asList(product1, product2);

        getProductOptionDto().stream()
            .map(ProductOptionDto::toEntity)
            .collect(Collectors.toList())
            .forEach(product2::addProductOption);

        when(productRepository.findAll()).thenReturn(productList);

        //when
        List<ProductsResponseDto> products = productService.findProducts();

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
        UpdateProductRequest updateRequest = new UpdateProductRequest(
            "침대 수정", 200000, "에이스 침대", 100, List.of(updateDto));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        //when
        productService.updateProduct(productId, updateRequest);

        //then
        Product updatedProduct = productService.findProductById(productId);
        assertThat(updatedProduct.getName()).isEqualTo("침대 수정");
        assertThat(updatedProduct.getDescription()).isEqualTo("에이스 침대");
        assertThat(updatedProduct.getProductOptions().size()).isEqualTo(1);
        assertThat(updatedProduct.getProductOptions().get(0).getColor()).isEqualTo("brown");

    }


    //옵션 생성 메서드
    private List<ProductOptionDto> getProductOptionDto() {

        ProductOption option1 = ProductOption.createOption( "white", "L", 30);
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