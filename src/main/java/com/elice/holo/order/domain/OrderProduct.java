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
import lombok.Data;

@Entity
@Data
public class OrderProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_product_id")
    private Long orderProductId;

    @ManyToOne(fetch = FetchType.LAZY) // 하나의 주문은 여러 제품을 포함할 수 있다.
    @JoinColumn(name = "order_id")
    private Order order; // 주문 엔티티와의 관계 설정

    @ManyToOne(fetch = FetchType.LAZY) // 하나의 제품은 여러 주문에 포함될 수 있다.
    @JoinColumn(name = "product_id")
    private Product productId; // 상품 ID

    private int count; // 상품 수량

}
