package com.elice.holo.cart.domain;


import com.elice.holo.product.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_product_id")
    private Long id; // PK

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart; // 장바구니

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // 상품

    @Column(nullable = false)
    private Long quantity; // 수량

    public CartProduct(Cart cart, Product product, Long quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public int getPrice() {
        return (int)(product.getPrice()*quantity);
    }

}

