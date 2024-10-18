package com.elice.holo.cart.Service;

import com.elice.holo.cart.domain.Cart;
import com.elice.holo.cart.domain.CartProduct;
import com.elice.holo.cart.dto.CartDto;
import com.elice.holo.cart.mapper.CartMapper;
import com.elice.holo.cart.repository.CartProductRepository;
import com.elice.holo.cart.repository.CartRepository;
import com.elice.holo.common.exception.CustomException;
import com.elice.holo.member.domain.Member;
import com.elice.holo.member.repository.MemberRepository;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.repository.ProductRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class CartService {


    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartProductRepository cartProductRepository;


    // 특정 회원의 장바구니 조회
    public CartDto getCartByMember(Long memberId) {
        // 회원 ID에 해당하는 장바구니를 조회
        Cart cart = cartRepository.findByMember_MemberId(memberId)
            .orElseThrow(() -> new CustomException("해당 회원의 장바구니가 존재하지 않습니다."));

        // 장바구니를 DTO로 변환하여 반환
        return cartMapper.toCartDto(cart);
    }

    //생성
    @Transactional
    public CartDto createCart(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException("존재하지 않는 회원입니다."));

        Cart newCart = Cart.createCart(member);
        cartRepository.save(newCart);

        return cartMapper.toCartDto(newCart);
    }

    //담기
    @Transactional
    public CartDto addProductToCartV2(Long memberId, Long cartId, Long productID, Long quantity,
        String color, String size) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException("존재하지 않는 회원입니다."));

        Product product = productRepository.findById(productID)
            .orElseThrow(() -> new CustomException("존재하지 않는 상품입니다."));

        // cartId를 통해 장바구니 조회
        Cart cart = cartRepository.findById(cartId)
            .orElseGet(() -> {
                // cartId가 없으면 새로 생성
                Cart newCart = Cart.createCart(member);
                cartRepository.save(newCart);
                return newCart;
            });
        // 장바구니에 상품 추가
        CartProduct cartProduct = new CartProduct(cart, product, quantity, color, size);

        // cartProduct를 데이터베이스에 저장
        cartProductRepository.save(cartProduct); // 여기에 cartProduct 저장
        return cartMapper.toCartDto(cart);
    }

    //전체 비우기
    @Transactional
    public void clearCart(Long memberId) {
        Cart cart = cartRepository.findByMember_MemberId(memberId)
            .orElseThrow(() -> new CustomException("장바구니가 존재하지 않습니다."));

        cart.getCartProducts().clear(); // 장바구니의 모든 상품을 제거
        cartRepository.save(cart); // 변경 사항 저장
    }

    //총가격//
    @Transactional
    public CartDto calculateTotalPrice(Long memberId) {
        Cart cart = cartRepository.findByMember_MemberId(memberId)
            .orElseThrow(() -> new CustomException("장바구니가 존재하지 않습니다."));

        List<CartProduct> cartProducts = cartProductRepository.findByCart_CartId(cart.getCartId());

        double totalPrice = cartProducts.stream()
            .mapToDouble(CartProduct::getPrice) // CartProduct의 getPrice 메서드 사용
            .sum();

        return new CartDto(cart.getCartId(), cartMapper.toCartProductDtoList(cartProducts),
            totalPrice); // DTO에 totalPrice 포함하여 반환
    }

    //상품 수량 수정//
    @Transactional
    public CartDto updateProductQuantityInCart(Long memberId, Long cartProductId, Long quantity) {
        // 장바구니에서 특정 상품 조회
        CartProduct cartProduct = cartProductRepository.findById(cartProductId)
            .orElseThrow(() -> new CustomException("장바구니에 해당 상품이 존재하지 않습니다."));

        // 수량 직접 수정
        cartProduct.updateQuantity(quantity); // 수량을 직접 변경

        // 변경된 카트 저장
        cartProductRepository.save(cartProduct);

        // 전체 장바구니 DTO 반환
        Cart cart = cartProduct.getCart(); // 장바구니 객체 가져오기
        return cartMapper.toCartDto(cart);
    }

    //선택삭제//
    @Transactional
    public void removeProductFromCart(Long memberId, Long cartProductId) {
        CartProduct cartProduct = cartProductRepository.findById(cartProductId)
            .orElseThrow(() -> new CustomException("장바구니에 해당 상품이 존재하지 않습니다."));

        // 해당 상품이 사용자 장바구니에 속하는지 확인
        if (!cartProduct.getCart().getMember().getMemberId().equals(memberId)) {
            throw new AccessDeniedException("이 상품을 삭제할 권한이 없습니다.");
        }

        cartProductRepository.delete(cartProduct); // 상품 삭제
    }


}


