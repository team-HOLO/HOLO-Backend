package com.elice.holo.product.dto;

import com.elice.holo.product.domain.ProductImage;
import lombok.Data;

@Data
public class ProductImageDto {

    private Long id;
    private String originName;
    private String storeName;
    private Boolean isThumbnail;

    public ProductImageDto(ProductImage productImage) {
        this.id = productImage.getProductImageId();;
        this.originName = productImage.getOriginName();
        this.storeName = productImage.getStoreName();
        this.isThumbnail = productImage.getIsThumbnail();
    }


}
