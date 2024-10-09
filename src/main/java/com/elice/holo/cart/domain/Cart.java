package com.elice.holo.cart.domain;


import com.elice.holo.member.domain.Member;
import com.elice.holo.product.domain.Product;
import jakarta.persistence.CascadeType;
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
    private Long cartId;

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
    public void addCartProduct(Product product, Long quantity) {
        Long cartProductId = generateCartProductId(); // 임시 ID 생성
        CartProduct cartProduct = new CartProduct(cartProductId, this, product, quantity);
        this.cartProducts.add(cartProduct);
    }

    private Long generateCartProductId() {
        return System.currentTimeMillis(); // 단순한 임시 ID
    }



    //장바구니 특정 상품 제거 //
    public void removeCartProduct(CartProduct cartProduct) {
        cartProducts.remove(cartProduct);
        cartProduct.clearCart(); // 장바구니를 null로 설정
    }

    //상품 수량 업데이트//
    public void updateCartProductQuantity(CartProduct cartProduct, Long quantity) {
        if (quantity <= 0) {
            removeCartProduct(cartProduct);
        } else {
            cartProduct.updateQuantity(quantity); // 수량 업데이트
        }
    }


}



