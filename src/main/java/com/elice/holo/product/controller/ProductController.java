package com.elice.holo.product.controller;

import com.elice.holo.product.dto.AddProductRequest;
import com.elice.holo.product.dto.AddProductResponse;
import com.elice.holo.product.dto.ProductResponseDto;
import com.elice.holo.product.dto.ProductSearchCond;
import com.elice.holo.product.dto.ProductsAdminResponseDto;
import com.elice.holo.product.dto.ProductsResponseDto;
import com.elice.holo.product.dto.SortBy;
import com.elice.holo.product.dto.UpdateProductRequest;
import com.elice.holo.product.service.ProductService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    //상품 등록
    @PostMapping("/products")
    public ResponseEntity<AddProductResponse> saveProduct(
        @Valid @RequestPart(name = "addProductRequest") AddProductRequest addProductRequest,
        @RequestPart(name = "productImages") List<MultipartFile> multipartFiles
    ) throws IOException {

        return new ResponseEntity<>(productService.saveProduct(addProductRequest, multipartFiles),
            HttpStatus.CREATED);
    }

    //상품 상세 조회
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponseDto> getProductDetails(
        @PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(productService.findProductById(id), HttpStatus.OK);
    }

    //메인 상품 목록 조회
    @GetMapping("/products")
    public ResponseEntity<Page<ProductsResponseDto>> getAllProducts(@ModelAttribute
    ProductSearchCond cond, Pageable pageable) {

        Page<ProductsResponseDto> products = productService.findProducts(pageable, cond);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    //카테고리별 상품 조회
    @GetMapping("/products/category/{categoryId}")
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

    //상품 수정
    @PutMapping("/products/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable(name = "id") Long id,
        @RequestPart UpdateProductRequest updateProductRequest,
        @RequestPart(name = "productImages", required = false) List<MultipartFile> multipartFiles
    ) {

        productService.updateProduct(id, updateProductRequest);

        return ResponseEntity.ok().build();
    }

    //상품 삭제
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") Long id) {
        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    //관리자용 페이지 조회
    @GetMapping("/admin/products")
    public ResponseEntity<Page<ProductsAdminResponseDto>> getProductAdminPage(Pageable pageable) {
        Page<ProductsAdminResponseDto> productAdminPage = productService.getProductAdminPage(
            pageable);

        return ResponseEntity.ok(productAdminPage);
    }
}
