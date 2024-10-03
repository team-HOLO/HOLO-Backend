package com.elice.holo.product.controller;

import com.elice.holo.product.controller.dto.AddProductRequest;
import com.elice.holo.product.controller.dto.AddProductResponse;
import com.elice.holo.product.controller.dto.ProductResponseDto;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    //상품 등록
    @PostMapping("/products")
    public ResponseEntity<AddProductResponse> saveProduct(
        @RequestBody AddProductRequest addProductRequest) {

        Product product = productService.saveProduct(addProductRequest.toEntity());

        return new ResponseEntity<>(new AddProductResponse(product), HttpStatus.CREATED);
    }

    //상품 단일 조회
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponseDto> getProductDetails(@PathVariable(name = "id") Long id) {
        Product findProduct = productService.findProductById(id);

        return new ResponseEntity<>(new ProductResponseDto(findProduct), HttpStatus.OK);
    }
}
