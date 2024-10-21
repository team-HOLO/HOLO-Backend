package com.elice.holo.product.controller;

import com.elice.holo.product.dto.ProductResponseDto;
import com.elice.holo.product.dto.ProductSearchCond;
import com.elice.holo.product.dto.ProductsResponseDto;
import com.elice.holo.product.dto.SortBy;
import com.elice.holo.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    //상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductDetails(
        @PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(productService.findProductById(id), HttpStatus.OK);
    }

    //메인 상품 목록 조회
    @GetMapping
    public ResponseEntity<Page<ProductsResponseDto>> getAllProducts(@ModelAttribute
    ProductSearchCond cond, Pageable pageable) {

        Page<ProductsResponseDto> products = productService.findProducts(pageable, cond);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    //카테고리별 상품 조회
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductsResponseDto>> getCategoryProducts(
        @PathVariable(name = "categoryId") Long categoryId,
        @ModelAttribute ProductSearchCond cond,
        @RequestParam(name = "sortBy", required = false) SortBy sort,
        Pageable pageable
    ) {
        Page<ProductsResponseDto> products = productService.findProductsByCategory(
            categoryId, cond, sort, pageable);

        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
