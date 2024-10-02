package com.elice.holo.product.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.elice.holo.product.domain.Product;
import com.elice.holo.product.repository.ProductRepository;
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
}