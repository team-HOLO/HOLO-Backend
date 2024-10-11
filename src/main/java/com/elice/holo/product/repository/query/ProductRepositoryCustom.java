package com.elice.holo.product.repository.query;

import com.elice.holo.product.dto.ProductSearchCond;
import com.elice.holo.product.dto.ProductsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    //상품 목록 화면용 쿼리(사용자)
    Page<ProductsResponseDto> findProductsPage(Pageable pageable, ProductSearchCond productSearchCond);
}
