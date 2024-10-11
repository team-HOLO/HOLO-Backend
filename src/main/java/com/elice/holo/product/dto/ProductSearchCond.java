package com.elice.holo.product.dto;

import lombok.Data;

//상품 이름 검색용 -> queryDsl
@Data
public class ProductSearchCond {
    private String productName;
}
