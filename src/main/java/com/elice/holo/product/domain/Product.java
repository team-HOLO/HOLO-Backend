package com.elice.holo.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Product {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int stockQuantity;

    private boolean isDeleted = false;

    @OneToMany(mappedBy = "product")
    private List<ProductImage> productImages = new ArrayList<>();

    //== 생성 메서드 ==//
    public static Product createProduct(String name, int price, String description, int stockQuantity
    ) {
        return Product.builder()
            .name(name)
            .price(price)
            .description(description)
            .stockQuantity(stockQuantity)
            .build();
    }

    //== 연관 관계 편의 메서드 ==//
    public void addProductImages(ProductImage productImage) {
        productImages.add(productImage);
        productImage.assignProduct(this);
    }

    //== 비즈니스 메서드 ==//
    //재고 검증을 통해 주문이 가능한지 검증
    public boolean canReduceStock(int quantity) {
        return this.stockQuantity >= quantity;
    }

    //주문이 성공하면 상품 재고 감소
    public void reduceStock(int quantity) {
        this.stockQuantity -= quantity;
    }

    //주문이 취소되면 상품 재고도 증가
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }


}
