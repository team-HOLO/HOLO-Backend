package com.elice.holo.product.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productOptionId;

    private String color;
    private String size;
    private int optionQuantity;
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    //== 옵션 생성 메서드 ==//
    public static ProductOption createOption(String color, String size, int optionQuantity) {
        return new ProductOption(color, size, optionQuantity);
    }

    public void assignProduct(Product product) {
        this.product = product;
    }

    private ProductOption(String color, String size, int optionQuantity) {
        this.color = color;
        this.size = size;
        this.optionQuantity = optionQuantity;
    }

    public void updateProductOption(String color, String size, int optionQuantity) {
        this.color = color;
        this.size = size;
        this.optionQuantity = optionQuantity;
    }

    public void updateIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}

