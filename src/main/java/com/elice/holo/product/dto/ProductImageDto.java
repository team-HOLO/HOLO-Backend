package com.elice.holo.product.dto;

import com.elice.holo.product.domain.ProductImage;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class ProductImageDto {

    private Long id;
    private String originName;
    private String storeName;
    private Boolean isThumbnail;
    private Long productId;

    public ProductImageDto(ProductImage productImage) {
        this.id = productImage.getProductImageId();;
        this.originName = productImage.getOriginName();
        this.storeName = productImage.getStoreName();
        this.isThumbnail = productImage.getIsThumbnail();
    }

    @QueryProjection
    public ProductImageDto(Long id, String originName, String storeName, Long productId) {
        this.id = id;
        this.originName = originName;
        this.storeName = storeName;
        this.productId = productId;
    }
}
