package com.elice.holo.product.domain;

import com.elice.holo.category.domain.Category;
import com.elice.holo.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@NoArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> productOptions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private Product(String name, int price, String description, int stockQuantity) {

        this.name = name;
        this.price = price;
        this.description = description;
        this.stockQuantity = stockQuantity;
    }

    //== 생성 메서드 ==//
    public static Product createProduct(String name, int price, String description, int stockQuantity
    ) {
        return new Product(name, price, description, stockQuantity);
    }

    //== 연관 관계 편의 메서드 ==//
    public void addProductImages(ProductImage productImage) {
        productImages.add(productImage);
        productImage.assignProduct(this);
    }

    public void addProductOption(ProductOption productOption) {
        productOptions.add(productOption);
        productOption.assignProduct(this);
    }

    public void addProductCategory(Category category) {
        this.category = category;
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

    //상품 수정 메서드
    public void updateProduct(String name, int price, String description, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stockQuantity = stockQuantity;
    }

    public void updateIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}
