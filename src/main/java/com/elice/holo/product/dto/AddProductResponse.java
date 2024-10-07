package com.elice.holo.product.dto;

import com.elice.holo.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

//상품 등록 Response DTO
@Data
@AllArgsConstructor
public class AddProductResponse {

    private Long productId;
    private String name;
    private int price;
    private String description;

    public AddProductResponse(Product product) {
        productId = product.getProductId();
        name = product.getName();
        price = product.getPrice();
        description = product.getDescription();
    }

}
