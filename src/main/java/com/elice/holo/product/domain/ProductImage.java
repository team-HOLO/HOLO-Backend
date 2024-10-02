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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProductImage {

    @Id
    @Column(name = "product_image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originName;  //원본 파일명

    @Column(nullable = false)
    private String storeName; //서버에 저장될 경로명

    @Column(nullable = false)
    private boolean isThumbnail = false; //대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    //==생성 메서드==//
    public static ProductImage createProductImage(String originName, String storeName) {
        return ProductImage.builder()
            .originName(originName)
            .storeName(storeName)
            .build();
    }

    public void assignProduct(Product product) {
        this.product = product;
    }

    public void changeIsThumbnail(boolean tf) {
        this.isThumbnail = tf;
    }
}
