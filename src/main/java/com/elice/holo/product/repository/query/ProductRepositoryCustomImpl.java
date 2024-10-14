package com.elice.holo.product.repository.query;

import static com.elice.holo.product.domain.QProduct.*;
import static com.elice.holo.product.domain.QProductImage.*;

import com.elice.holo.product.domain.QProductImage;
import com.elice.holo.product.dto.ProductImageDto;
import com.elice.holo.product.dto.ProductSearchCond;
import com.elice.holo.product.dto.ProductsResponseDto;
import com.elice.holo.product.dto.QProductImageDto;
import com.elice.holo.product.dto.QProductsResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class ProductRepositoryCustomImpl implements ProductRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<ProductsResponseDto> findProductsPage(Pageable pageable, ProductSearchCond cond) {
        List<ProductsResponseDto> products = queryFactory
            .select(new QProductsResponseDto(
                product.productId,
                product.name,
                product.price
            ))
            .from(product)
            .where(productNameIn(cond))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        Map<Long, List<ProductImageDto>> productsThumbnails = getProductsThumbnails(
            getProductsIds(products));

        products.forEach(p -> p.setThumbNailImage(productsThumbnails.get(p.getProductId())));

        JPAQuery<Long> countQuery = queryFactory
            .select(product.count())
            .from(product)
            .where(productNameIn(cond));

        return PageableExecutionUtils.getPage(products, pageable, countQuery::fetchOne);
    }

    private List<Long> getProductsIds(List<ProductsResponseDto> productsResponseDtos) {
        return productsResponseDtos.stream()
            .map(ProductsResponseDto::getProductId)
            .collect(Collectors.toList());
    }


    public  Map<Long, List<ProductImageDto>> getProductsThumbnails(List<Long> productIds) {
        List<ProductImageDto> productImages = queryFactory
            .select(new QProductImageDto(
                productImage.productImageId,
                productImage.originName,
                productImage.storeName,
                productImage.product.productId
            ))
            .from(productImage)
            .where(productImage.product.productId.in(productIds),
                productImage.isThumbnail.isTrue()
            )
            .fetch();

        Map<Long, List<ProductImageDto>> ProductImageMap = productImages.stream()
            .collect(Collectors.groupingBy(ProductImageDto::getProductId));

        return ProductImageMap;
    }

    private BooleanBuilder productNameIn(ProductSearchCond cond) {
        return nullSafeBuilder(() -> product.name.contains(cond.getProductName()));
    }

    public static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (IllegalArgumentException | NullPointerException e) {
            return new BooleanBuilder();
        }
    }


}
