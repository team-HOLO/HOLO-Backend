package com.elice.holo.cart.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.elice.holo.cart.Service.CartService;
import com.elice.holo.cart.domain.Cart;
import com.elice.holo.cart.domain.CartProduct;
import com.elice.holo.cart.dto.CartDto;
import com.elice.holo.cart.mapper.CartMapper;
import com.elice.holo.cart.repository.CartRepository;
import com.elice.holo.member.domain.Member;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.repository.ProductRepository;
import java.util.ArrayList;
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
    private ProductRepository productRepository;

    @Mock
    private CartMapper cartMapper;

    private Member member;
    private Cart cart;
    private Product product;


    @BeforeEach
    void setUp() {
        member = new Member("test@example.com", "password", "Test User", false, false,
            "010-1234-5678", true, 30);
        cart = Cart.createCart(member);
        product = Product.createProduct("Test Product", 1000, "Description", 10);
    }


    @DisplayName("특정 회원의 장바구니 조회 테스트")
    @Test
    void testGetCartByMember() {
        double totalPrice = 130;

        when(cartRepository.findByMember_MemberId(member.getMemberId())).thenReturn(cart);
        when(cartMapper.toCartDto(cart)).thenReturn(
            new CartDto(cart.getCartId(), member.getMemberId(), new ArrayList<>(),totalPrice));

        CartDto result = cartService.getCartByMember(member);

        assertEquals(cart.getCartId(), result.getCartId());
        assertEquals(member.getMemberId(), result.getMemberId());
    }

    @DisplayName("장바구니 생성 테스트")
    @Test
    void testCreateCart() {
        double totalPrice = 130;
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toCartDto(cart)).thenReturn(
            new CartDto(cart.getCartId(), member.getMemberId(), new ArrayList<>(),totalPrice));

        CartDto result = cartService.createCart(member);

        assertEquals(cart.getCartId(), result.getCartId());
        assertEquals(member.getMemberId(), result.getMemberId());
    }

    @Test
    @DisplayName("장바구니에 상품 추가 테스트")
    void testAddProductToCart() {
        double totalPrice = 130;
        when(cartRepository.findById(cart.getCartId())).thenReturn(java.util.Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(java.util.Optional.of(product));
        when(cartMapper.toCartDto(cart)).thenReturn(new CartDto(cart.getCartId(), member.getMemberId(), new ArrayList<>(),totalPrice));

        CartDto result = cartService.addProductToCart(cart.getCartId(), product.getId(), 2L);

        verify(cartRepository).save(cart);
        assertEquals(cart.getCartId(), result.getCartId());
    }

    @Test
    @DisplayName("장바구니에서 상품 제거 테스트")
    void testRemoveProductFromCart() {
        double totalPrice = 130;
        // 먼저 상품을 추가
        cart.addCartProduct(product, 1L);

        // cartProduct는 cart에 추가된 상태에서 가져와야 합니다.
        CartProduct cartProduct = cart.getCartProducts().get(0);

        // cartProduct의 cart_productId가 null인지 확인
        assertNotNull(cartProduct.getCartProductId(), "CartProduct ID should not be null");

        when(cartRepository.findById(cart.getCartId())).thenReturn(java.util.Optional.of(cart));
        when(cartMapper.toCartDto(cart)).thenReturn(new CartDto(cart.getCartId(), member.getMemberId(), new ArrayList<>(),totalPrice));

        CartDto result = cartService.removeProductFromCart(cart.getCartId(), cartProduct.getCartProductId());

        // 제거 후 cartProduct가 실제로 제거되었는지 확인
        assertEquals(0, cart.getCartProducts().size());

        assertEquals(cart.getCartId(), result.getCartId());
        assertEquals(member.getMemberId(), result.getMemberId());
        verify(cartRepository).save(cart);
    }


    @Test
    @DisplayName("상품 수량 업데이트 테스트")
    void testUpdateProductQuantity() {

        cart.addCartProduct(product, 1L); // 상품 추가


        when(cartRepository.findById(cart.getCartId())).thenReturn(java.util.Optional.of(cart));

        cartService.updateProductQuantity(cart.getCartId(), cart.getCartProducts().get(0).getCartProductId(), 2L);


        assertEquals(2L, cart.getCartProducts().get(0).getQuantity());
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("장바구니 전체 비우기 테스트")
    void testClearCart() {
        when(cartRepository.findById(cart.getCartId())).thenReturn(java.util.Optional.of(cart));

        cartService.clearCart(cart.getCartId());

        verify(cartRepository).save(cart);
        assertEquals(0, cart.getCartProducts().size());
    }
    @Test
    @DisplayName("장바구니 총 가격 계산 테스트")
    void testCalculateTotalPrice() {
        cart.addCartProduct(product, 2L); // 상품 추가

        when(cartRepository.findById(cart.getCartId())).thenReturn(java.util.Optional.of(cart));

        double totalPrice = cartService.calculateTotalPrice(cart.getCartId());

        assertEquals(2000.0, totalPrice);
    }

}


