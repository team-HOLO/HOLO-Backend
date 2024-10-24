package com.elice.holo.product.controller;

import com.elice.holo.member.domain.MemberDetails;
import com.elice.holo.member.exception.AccessDeniedException;
import com.elice.holo.product.dto.AddProductRequest;
import com.elice.holo.product.dto.AddProductResponse;
import com.elice.holo.product.dto.ProductsAdminResponseDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class ProductAdminController {

    private final ProductService productService;

    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "상품 등록 성공"),
        @ApiResponse(responseCode = "403", description = "상품 등록 권한 없음"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<AddProductResponse> saveProduct(
        @Valid @RequestPart(name = "addProductRequest") AddProductRequest addProductRequest,
        @RequestPart(name = "productImages") List<MultipartFile> multipartFiles
    ) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 상품 등록 권한 확인
        if (!memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("상품 등록 권한이 없습니다.");
        }

        return new ResponseEntity<>(productService.saveProduct(addProductRequest, multipartFiles),
            HttpStatus.CREATED);
    }

    @Operation(summary = "관리자 상품 목록 조회", description = "관리자 페이지에서 상품 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<Page<ProductsAdminResponseDto>> getProductAdminPage(Pageable pageable) {
        Page<ProductsAdminResponseDto> productAdminPage = productService.getProductAdminPage(pageable);
        return ResponseEntity.ok(productAdminPage);
    }

    @Operation(summary = "상품 수정", description = "주어진 ID에 해당하는 상품 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "상품 수정 성공"),
        @ApiResponse(responseCode = "404", description = "상품 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(
        @Parameter(description = "상품 ID") @PathVariable(name = "id") Long id,
        @RequestPart UpdateProductRequest updateProductRequest,
        @RequestPart(name = "productImages", required = false) List<MultipartFile> multipartFiles
    ) {
        productService.updateProduct(id, updateProductRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "상품 삭제", description = "주어진 ID에 해당하는 상품을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "상품 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "상품 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@Parameter(description = "상품 ID") @PathVariable(name = "id") Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
