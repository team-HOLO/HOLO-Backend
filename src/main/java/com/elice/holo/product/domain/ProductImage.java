package com.elice.holo.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productImageId;

    @Column(nullable = false)
    private String originName;  //원본 파일명

    @Column(nullable = false)
    private String storeName; //서버에 저장될 파일명

    @Column(nullable = false)
    private Boolean isThumbnail = false; //대표 이미지 여부

    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private ProductImage(String originName, String storeName) {
        this.originName = originName;
        this.storeName = storeName;
    }

    //==생성 메서드==//
    public static ProductImage createProductImage(String originName, String storeName) {
        return new ProductImage(originName, storeName);
    }

    //==연관 관계 편의 메서드==//
    public void assignProduct(Product product) {
        this.product = product;
        product.getProductImages().add(this);
    }

    public void changeIsThumbnail(boolean tf) {
        this.isThumbnail = tf;
    }
}
