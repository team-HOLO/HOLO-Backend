package com.elice.holo.product.dto;

import com.elice.holo.product.domain.Product;
import com.elice.holo.product.domain.ProductImage;
import lombok.Data;

/**
 * 상품 목록 조회
 * 상품 이름, 가격만 필요
 * 상품 목록을 조회할 시 모든 상품 이미지가 필요 X, 썸네일 이미지만 가져와야함
 *
 */
@Data
public class ProductsResponseDto {

    private Long productId;
    private String name;
    private int price;
    private ProductImageDto thumbNailImage;  //TODO 썸네일 이미지 개수 결정

    public ProductsResponseDto(Product product) {
        productId = product.getProductId();
        name = product.getName();
        price = product.getPrice();
        thumbNailImage = product.getProductImages().stream()
            .filter(ProductImage::getIsThumbnail)
            .map(ProductImageDto::new)
            .findFirst().get();

    }
}
