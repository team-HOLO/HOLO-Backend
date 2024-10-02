package com.elice.holo.product.controller;

import com.elice.holo.product.controller.dto.AddProductRequest;
import com.elice.holo.product.controller.dto.AddProductResponse;
import com.elice.holo.product.controller.dto.ProductResponseDto;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;


    @PostMapping("/products")  //상품 등록 API
    public ResponseEntity<AddProductResponse> saveProduct(@RequestBody AddProductRequest addProductRequest) {

        Product product = productService.saveProduct(addProductRequest.toEntity());

        return new ResponseEntity<>(new AddProductResponse(product), HttpStatus.CREATED);
    }

}
