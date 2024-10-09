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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartProductId; // PK

    @ManyToOne
    @JoinColumn(name = "cartId", nullable = false)
    private Cart cart; // 장바구니

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product; // 상품

    @Column(nullable = false)
    private Long quantity; // 수량

    public CartProduct(Long cartProductIdCart,Cart cart, Product product, Long quantity) {
        this.cartProductId=cartProductIdCart;
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public int getPrice() {
        return (product != null && quantity != null) ? (int)(product.getPrice() * quantity) : 0;
    }


    public void updateQuantity(Long quantity) {
        this.quantity = quantity; // 장바구니 업데이트
    }

    public void clearCart() {
        this.cart = null; // 장바구니를 null로 설정
    }

}

