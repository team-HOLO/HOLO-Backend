package com.elice.holo.product.repository.query;

import com.elice.holo.product.domain.Product;
import com.elice.holo.product.dto.ProductSearchCond;
import com.elice.holo.product.dto.ProductsResponseDto;
import com.elice.holo.product.dto.SortBy;
import com.elice.holo.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    //상품 목록 화면용 쿼리(사용자)
    Page<ProductsResponseDto> findProductsPage(Pageable pageable, ProductSearchCond productSearchCond);

    //관리자 페이지용 쿼리
    Page<Product> findAdminPage(Pageable pageable);


    //카테고리별 목록
    Page<ProductsResponseDto> findCategoryProductsPage(Pageable pageable,
        ProductSearchCond productSearchCond, Long categoryId, SortBy sort);
}
