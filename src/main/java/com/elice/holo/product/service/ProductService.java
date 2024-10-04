package com.elice.holo.product.service;

import com.elice.holo.product.service.dto.AddProductRequest;
import com.elice.holo.product.service.dto.ProductOptionDto;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.exception.ProductNotFoundException;
import com.elice.holo.product.repository.ProductRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 추가를 위한 메서드
     * @param addProductRequest
     * @return Product
     */
    @Transactional
    public Product saveProduct(AddProductRequest addProductRequest) {

        Product newProduct = addProductRequest.toEntity();

        //옵션 리스트 받아와서 Product 에 추가
        addProductRequest.getProductOptions().stream()
            .map(ProductOptionDto::toEntity)
            .collect(Collectors.toList()).forEach(newProduct::addProductOption);

        return productRepository.save(newProduct);
    }

    /**
     * 상품 단일 조회(상세 조회)를 위한 메서드
     * @param id
     * @return Product
     * @throws ProductNotFoundException 상품이 존재하지 않을 경우
     */
    public Product findProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("상품이 존재하지 않습니다."));
    }



}
