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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class ProductAdminController {

    private final ProductService productService;

    //상품 등록
    @PostMapping
    public ResponseEntity<AddProductResponse> saveProduct(
        @Valid @RequestPart(name = "addProductRequest") AddProductRequest addProductRequest,
        @RequestPart(name = "productImages") List<MultipartFile> multipartFiles
    ) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        //상품 등록 생성 권한 확인
        if (!memberDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("상품 등록 권한이 없습니다.");
        }

        return new ResponseEntity<>(productService.saveProduct(addProductRequest, multipartFiles),
            HttpStatus.CREATED);
    }

    //관리자용 페이지 조회
    @GetMapping
    public ResponseEntity<Page<ProductsAdminResponseDto>> getProductAdminPage(Pageable pageable) {
        Page<ProductsAdminResponseDto> productAdminPage = productService.getProductAdminPage(
            pageable);

        return ResponseEntity.ok(productAdminPage);
    }

    //상품 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable(name = "id") Long id,
        @RequestPart UpdateProductRequest updateProductRequest,
        @RequestPart(name = "productImages", required = false) List<MultipartFile> multipartFiles
    ) {

        productService.updateProduct(id, updateProductRequest);

        return ResponseEntity.ok().build();
    }

    //상품 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") Long id) {
        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}




