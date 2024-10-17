package com.elice.holo.product.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.elice.holo.product.dto.QProductImageDto is a Querydsl Projection type for ProductImageDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QProductImageDto extends ConstructorExpression<ProductImageDto> {

    private static final long serialVersionUID = 901065976L;

    public QProductImageDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> originName, com.querydsl.core.types.Expression<String> storeName, com.querydsl.core.types.Expression<Long> productId) {
        super(ProductImageDto.class, new Class<?>[]{long.class, String.class, String.class, long.class}, id, originName, storeName, productId);
    }

}

