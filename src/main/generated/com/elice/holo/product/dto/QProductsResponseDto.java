package com.elice.holo.product.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.elice.holo.product.dto.QProductsResponseDto is a Querydsl Projection type for ProductsResponseDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QProductsResponseDto extends ConstructorExpression<ProductsResponseDto> {

    private static final long serialVersionUID = 1875530687L;

    public QProductsResponseDto(com.querydsl.core.types.Expression<Long> productId, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<Integer> price) {
        super(ProductsResponseDto.class, new Class<?>[]{long.class, String.class, int.class}, productId, name, price);
    }

}

