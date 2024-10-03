package com.elice.holo.cart.domain;


import com.elice.holo.member.domain.Member;
import com.elice.holo.product.domain.Product;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartProduct> cartProducts = new ArrayList<>(); // 장바구니 상품들

    @Builder
    private Cart(Member member) {
        this.member = member;
    }

    //생성 메서드//
    public static Cart createCart(Member member) {
        return Cart.builder()
            .member(member)
            .build();
    }
    //새로운 상품을 장바구니에 추가//
    public void addCartPoduct(Product product, Long quantity) {
        CartProduct cartProduct = new CartProduct(this, product, quantity);
        cartProducts.add(cartProduct);
    }
    //장바구니 특정 상품 제거 //
    public void removeCartProduct(CartProduct cartProduct) {
        cartProducts.remove(cartProduct);
        cartProduct.setCart(null);
    }

    //상품 수량 업데이트//
    public void updateCartProductQuantity(CartProduct cartProduct, Long quantity) {
        if (quantity <= 0) {
            removeCartProduct(cartProduct);
        } else {
            cartProduct.setQuantity(quantity);
        }

    }
}



