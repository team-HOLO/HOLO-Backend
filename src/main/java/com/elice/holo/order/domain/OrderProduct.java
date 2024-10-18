package com.elice.holo.order.domain;

import com.elice.holo.product.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Builder
@Getter
@AllArgsConstructor
@Table(name = "order_product")
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id")
    private Long orderProductId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String size;

    // 팩토리 메서드
    public static OrderProduct createOrderProduct(Product product, int quantity, String color, String size) {
        if (!product.canReduceStock(quantity)) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        product.reduceStock(quantity);  // 재고 감소
        return new OrderProduct(product, quantity, color, size);
    }
    // private 생성자
    private OrderProduct(Product product, int quantity, String color, String size) {
        this.product = product;
        this.quantity = quantity;
        this.color = color;
        this.size = size;
    }

}