package com.elice.holo.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.elice.holo.product.domain.Product;
import com.elice.holo.product.domain.ProductOption;
import com.elice.holo.product.exception.ProductNotFoundException;
import com.elice.holo.product.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        when(productRepository.save(any(Product.class))).thenReturn(product);

        //when
        Product savedProduct = productService.saveProduct(product);

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

        List<ProductOption> productOption = getProductOption();
        for (ProductOption option : productOption) {
            product.addProductOption(option);
        }


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

    //옵션 생성 메서드
    private List<ProductOption> getProductOption() {

        ProductOption option1 = ProductOption.createOption("white", "L", 30);
        ProductOption option2 = ProductOption.createOption("black", "L", 30);

        List<ProductOption> optionList = new ArrayList<>();
        optionList.add(option1);
        optionList.add(option2);

        return optionList;
    }
}