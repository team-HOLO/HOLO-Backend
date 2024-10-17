package com.elice.holo.cart.Service;

import com.elice.holo.cart.domain.Cart;
import com.elice.holo.cart.domain.CartProduct;
import com.elice.holo.cart.dto.CartDto;
import com.elice.holo.cart.dto.CartRequestDto;
import com.elice.holo.cart.exception.CartNotFoundException;
import com.elice.holo.cart.mapper.CartMapper;
import com.elice.holo.cart.repository.CartRepository;
import com.elice.holo.common.exception.CustomException;
import com.elice.holo.common.exception.ErrorCode;
import com.elice.holo.member.domain.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;


    // 특정 회원의 장바구니 조회
    public CartDto getCartByMember(Member member) {
        Cart cart = cartRepository.findByMember_MemberId(member.getMemberId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 장바구니입니다"));
        return cartMapper.toCartDto(cart);
    }

    //장바구니 생성
    @Transactional
    public CartDto createCart() {
        Cart cart = Cart.createCart();
        cart = cartRepository.save(cart);
        return cartMapper.toCartDto(cart);
    }

    //장바구니에 상품 추가
    @Transactional
    public CartDto addProductToCart(Long cartId, CartRequestDto cartRequest) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(
                () -> new CartNotFoundException(ErrorCode.CART_NOT_FOUND, ("존재하지 않는 장바구니입니다")));
        CartProduct cartProduct = cartMapper.toEntity(cartRequest);
        cart.addCartProduct(cartProduct.getProduct(), cartProduct.getQuantity(),
            cartRequest.getColor(), cartRequest.getSize());

        cartRepository.save(cart);

        return cartMapper.toCartDto(cart);
    }

    //장바구니에서 상품 제거
    @Transactional
    public CartDto removeProductFromCart(Long cartId, Long cartProductId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(
                () -> new CartNotFoundException(ErrorCode.CART_NOT_FOUND, ("존재하지 않는 장바구니입니다")));
        CartProduct cartProduct = cart.getCartProducts().stream()
            .filter(cp -> cp.getCartProductId().equals(cartProductId))
            .findFirst()
            .orElseThrow(
                () -> new CartNotFoundException(ErrorCode.CART_NOT_FOUND, ("존재하지 않는 장바구니입니다")));

        cart.removeCartProduct(cartProduct);
        cartRepository.save(cart);
        return cartMapper.toCartDto(cart);
    }

    //상품 수량 업데이트
    @Transactional
    public CartDto updateProductQuantity(Long cartId, Long cartProductId, Long quantity) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(
                () -> new CartNotFoundException(ErrorCode.CART_NOT_FOUND, ("존재하지 않는 장바구니입니다")));

        CartProduct cartProduct = cart.getCartProducts().stream()
            .filter(cp -> cp.getCartProductId().equals(cartProductId))
            .findFirst()
            .orElseThrow(
                () -> new CartNotFoundException(ErrorCode.CART_NOT_FOUND, ("존재하지 않는 장바구니입니다")));

        if (quantity == null || quantity <= 0) {
            throw new CustomException("수량은 1 이상이어야 합니다.");
        }

        cart.updateCartProductQuantity(cartProduct, quantity);
        cartRepository.save(cart);

        return cartMapper.toCartDto(cart); // 수정된 장바구니 반환
    }

    //장바구니 전체 비우기
    @Transactional
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(
                () -> new CartNotFoundException(ErrorCode.CART_NOT_FOUND, ("존재하지 않는 장바구니입니다")));

        cart.getCartProducts().clear();
        cartRepository.save(cart);
    }


    //장바구니의 총 가격을 계산하는 메서드
    public double calculateTotalPrice(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(
                () -> new CartNotFoundException(ErrorCode.CART_NOT_FOUND, ("존재하지 않는 장바구니입니다")));
        return cart.getCartProducts().stream()
            .mapToInt(CartProduct::getPrice)
            .sum();
    }


}


