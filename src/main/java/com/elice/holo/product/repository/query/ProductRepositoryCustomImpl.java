package com.elice.holo.product.repository.query;

import static com.elice.holo.category.domain.QCategory.*;
import static com.elice.holo.product.domain.QProduct.*;
import static com.elice.holo.product.domain.QProductImage.*;

import com.elice.holo.product.domain.Product;
import com.elice.holo.product.dto.ProductImageDto;
import com.elice.holo.product.dto.ProductSearchCond;
import com.elice.holo.product.dto.ProductsResponseDto;
import com.elice.holo.product.dto.QProductImageDto;
import com.elice.holo.product.dto.QProductsResponseDto;
import com.elice.holo.product.dto.SortBy;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
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

    //메인 페이지 조회
    @Override
    public Page<ProductsResponseDto> findProductsPage(Pageable pageable, ProductSearchCond cond) {

        List<ProductsResponseDto> products = getMainProductsResponse(pageable, cond);

        if (!products.isEmpty()) {
            Map<Long, List<ProductImageDto>> productsThumbnails = getProductsThumbnails(
                getProductsIds(products));

            products.forEach(p -> p.setThumbNailImage(productsThumbnails.get(p.getProductId())));
        }

        JPAQuery<Long> countQuery = queryFactory
            .select(product.count())
            .from(product)
            .where(productNameFilter(cond));

        return PageableExecutionUtils.getPage(products, pageable, countQuery::fetchOne);
    }

    private List<ProductsResponseDto> getMainProductsResponse(Pageable pageable,
        ProductSearchCond cond) {

        return queryFactory
            .select(new QProductsResponseDto(
                product.productId,
                product.name,
                product.price
            ))
            .from(product)
            .where(productNameFilter(cond), product.isDeleted.isFalse())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
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

    private BooleanBuilder productNameFilter(ProductSearchCond cond) {
        return nullSafeBuilder(() -> product.name.contains(cond.getProductName()))
            .or(nullSafeBuilder(() ->product.description.contains(cond.getProductName())));
    }

    public static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (IllegalArgumentException | NullPointerException e) {
            return new BooleanBuilder();
        }
    }

    //관리자 페이지 조회
    @Override
    public Page<Product> findAdminPage(Pageable pageable) {
        List<Product> result = queryFactory
            .selectFrom(product)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(product.count())
            .from(product);

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }


    //카테고리별 상품 조회
    @Override
    public Page<ProductsResponseDto> findCategoryProductsPage(Pageable pageable,
        ProductSearchCond cond, Long categoryId, SortBy sort) {

        List<ProductsResponseDto> products = getCategoryProductsResponse(
            pageable, cond, categoryId, sort);

        if (!products.isEmpty()) {
            Map<Long, List<ProductImageDto>> productsThumbnails = getProductsThumbnails(
                getProductsIds(products));

            products.forEach(p -> p.setThumbNailImage(productsThumbnails.get(p.getProductId())));
        }

        JPAQuery<Long> countQuery = queryFactory
            .select(product.count())
            .from(product)
            .where(getCategoryCond(cond, categoryId));

        return PageableExecutionUtils.getPage(products, pageable, countQuery::fetchOne);
    }

    private List<ProductsResponseDto> getCategoryProductsResponse(Pageable pageable,
        ProductSearchCond cond, Long categoryId, SortBy sort) {

        return queryFactory
            .select(new QProductsResponseDto(
                product.productId,
                product.name,
                product.price
            ))
            .from(product)
            .where(getCategoryCond(cond, categoryId))
            .orderBy(getOrderBy(sort))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();
    }

    private BooleanBuilder getCategoryCond(ProductSearchCond cond, Long categoryId) {
        return productNameFilter(cond)  //검색 필터링
            .and(categoryFilter(categoryId)) //카테고리 필터링
            .and(product.isDeleted.isFalse());

    }

    private BooleanBuilder categoryFilter(Long categoryId) {
        return nullSafeBuilder(() -> product.category.categoryId.in(findCategoryIds(categoryId)));
    }

    private OrderSpecifier<?> getOrderBy(SortBy sort) {
        if (sort == null) {
            return product.createdAt.desc();
        }

        switch(sort) {
            case LATEST :
                return product.createdAt.desc();
            case NAME :
                return product.name.asc();
            case PRICE_ASC:
                return product.price.desc();
            case PRICE_DESC:
                return product.price.asc();
            default:
                return product.createdAt.desc();
        }
    }

    private List<Long> findCategoryIds(Long categoryId) {
        return queryFactory
            .select(category.categoryId)
            .from(category)
            .where(category.parentCategory.categoryId.eq(categoryId).or(category.categoryId.eq(categoryId)))
            .fetch();
    }
}
