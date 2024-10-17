package com.elice.holo.cart.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elice.holo.cart.Service.CartService;
import com.elice.holo.cart.domain.Cart;
import com.elice.holo.cart.domain.CartProduct;
import com.elice.holo.cart.dto.CartDto;
import com.elice.holo.cart.dto.CartRequestDto;
import com.elice.holo.cart.mapper.CartMapper;
import com.elice.holo.cart.repository.CartRepository;
import com.elice.holo.product.domain.Product;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartMapper cartMapper;

    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        // Test Product 생성

        product = Product.createProduct("Test Product", 1000, "Description", 10);
        // Test Cart 생성
        cart = Cart.createCart(); // createCart 메서드 호출
        cart.addCartProduct(product, 2L, "red", "M"); // 상품 추가
    }

    @DisplayName("장바구니 생성 테스트")
    @Test
    void testCreateCart() {
        Long memberId = 1L;
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toCartDto(cart)).thenReturn(
            new CartDto(null, memberId, new ArrayList<>(), 0.0));

        CartDto result = cartService.createCart();

        assertNotNull(result);
        assertEquals(cart.getCartId(), result.getCartId());
    }

//    @DisplayName("특정 회원의 장바구니 조회 테스트")
//    @Test
//    void testGetCartByMember() {
//        when(cartRepository.findByMember_MemberId(anyLong())).thenReturn(Optional.of(cart));
//        when(cartMapper.toCartDto(cart)).thenReturn(
//            new CartDto(cart.getCartId(), new ArrayList<>(), 0.0));
//
//        CartDto result = cartService.getCartByMember(
//            new Member(1L, "test@example.com", "password", "Test User"));
//
//        assertEquals(cart.getCartId(), result.getCartId());
//    }

    @DisplayName("장바구니에 상품 추가 테스트")
    @Test
    void testAddProductToCart() {
        Long memberId = 1L;
        CartRequestDto cartRequest = new CartRequestDto(1L, 1, "red", "M");

        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(cartMapper.toEntity(cartRequest)).thenReturn(
            new CartProduct(null, cart, product, 2L, "red", "M"));
        when(cartMapper.toCartDto(cart)).thenReturn(
            new CartDto(cart.getCartId(), memberId, new ArrayList<>(), 0.0));

        CartDto result = cartService.addProductToCart(cart.getCartId(), cartRequest);

        assertNotNull(result);
        assertEquals(cart.getCartId(), result.getCartId());
    }

    @DisplayName("장바구니에서 상품 제거 테스트")
    @Test
    void testRemoveProductFromCart() {
        CartProduct cartProduct = new CartProduct(1L, cart, product, 2L, "red", "M");
        cart.addCartProduct(product, 2L, "red", "M"); // 장바구니에 추가

        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));

        cartService.removeProductFromCart(cart.getCartId(), cartProduct.getCartProductId());

        verify(cartRepository).save(cart);
        assertFalse(cart.getCartProducts().contains(cartProduct),
            "Cart should not contain the removed product");
    }

    @DisplayName("상품 수량 업데이트 테스트")
    @Test
    void testUpdateProductQuantity() {
        CartRequestDto cartRequest = new CartRequestDto(1L, 1, "red", "M");
        cart.addCartProduct(product, 2L, "red", "M");

        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));

        cartService.updateProductQuantity(cart.getCartId(),
            cart.getCartProducts().get(0).getCartProductId(), 3L);

        assertEquals(3L, cart.getCartProducts().get(0).getQuantity());
        verify(cartRepository).save(cart);
    }

    @DisplayName("장바구니 전체 비우기 테스트")
    @Test
    void testClearCart() {
        cart.addCartProduct(product, 2L, "red", "M");

        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));

        cartService.clearCart(cart.getCartId());

        verify(cartRepository).save(cart);
        assertEquals(0, cart.getCartProducts().size());
    }

    @DisplayName("장바구니 총 가격 계산 테스트")
    @Test
    void testCalculateTotalPrice() {
        cart.addCartProduct(product, 2L, "red", "M");

        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));

        double totalPrice = cartService.calculateTotalPrice(cart.getCartId());

        assertEquals(2000.0, totalPrice);
    }
}


