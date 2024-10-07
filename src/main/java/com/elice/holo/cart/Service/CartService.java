package com.elice.holo.cart.Service;

import com.elice.holo.cart.domain.Cart;
import com.elice.holo.cart.domain.CartProduct;
import com.elice.holo.cart.repository.CartRepository;
import com.elice.holo.member.domain.Member;
import com.elice.holo.member.repository.MemberRepository;
import com.elice.holo.product.domain.Product;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CartService {

    private final CartRepository cartRepository;

    @Autowired
    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    //특정 회원의 장바구니 조회
    public Cart getCartByMember(Member member){
        return cartRepository.findByMemberId(member.getMemberId());
    }

    //장바구니 생성
    @Transactional
    public Cart createCart(Member member){
        Cart cart = Cart.createCart(member);
        return cartRepository.save(cart);
    }

    //장바구니에 상품 추가
    @Transactional
    public void addProductToCart(Cart cart, Product product, Long quantity) {
        cart.addCartPoduct(product,quantity);
        cartRepository.save(cart);
    }

    //장바구니에서 상품 제거
    @Transactional
    public void removeProductFromCart(Cart cart, CartProduct cartProduct) {
        cart.removeCartProduct(cartProduct);
        cartRepository.save(cart);
    }

    //상품 수량 업데이트
    @Transactional
    public void updateProductQuantity(Cart cart, CartProduct cartProduct, Long quantity) {
        cart.updateCartProductQuantity(cartProduct, quantity);
        cartRepository.save(cart);
    }

    //장바구니 전체 비우기
    @Transactional
    public void clearCart(Cart cart) {
        cart.getCartProducts().clear();
        cartRepository.save(cart);
    }

    //특정 상품 삭제
    @Transactional
    public void removeSpecificProducts(Cart cart, List<CartProduct> productsToRemove) {
        for (CartProduct cartProduct : productsToRemove) {
            cart.removeCartProduct(cartProduct);
        }
        cartRepository.save(cart);
    }
    //장바구니의 총 가격을 계산하는 메서드
    public double calculateTotalPrice(Cart cart) {
        return cart.getCartProducts().stream()
            .mapToInt(CartProduct::getPrice)
            .sum();
    }
}


