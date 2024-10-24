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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 상세 조회", description = "주어진 ID에 해당하는 상품의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상품 상세 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "상품 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductDetails(
        @Parameter(description = "상품 ID") @PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(productService.findProductById(id), HttpStatus.OK);
    }

    @Operation(summary = "상품 목록 조회", description = "모든 상품을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<Page<ProductsResponseDto>> getAllProducts(
        @ModelAttribute ProductSearchCond cond, Pageable pageable) {

        Page<ProductsResponseDto> products = productService.findProducts(pageable, cond);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "카테고리별 상품 조회", description = "주어진 카테고리에 해당하는 상품 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "카테고리별 상품 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductsResponseDto>> getCategoryProducts(
        @Parameter(description = "카테고리 ID") @PathVariable(name = "categoryId") Long categoryId,
        @ModelAttribute ProductSearchCond cond,
        @RequestParam(name = "sortBy", required = false) SortBy sort,
        Pageable pageable) {

        Page<ProductsResponseDto> products = productService.findProductsByCategory(
            categoryId, cond, sort, pageable);

        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
