package com.elice.holo.cart.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
    private ProductRepository productRepository;

    @Mock
    private CartMapper cartMapper;

    private Member member;
    private Cart cart;
    private Product product;


    @BeforeEach
    void setUp() {
        member = new Member(null, "test@example.com", "password", "Test User", false, false, "010-1234-5678", 30, true);
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
        double totalPrice=130;
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
        Product product = mock(Product.class);
        when(product.getProductId()).thenReturn(1L);

        when(cartRepository.findById(cart.getCartId())).thenReturn(java.util.Optional.of(cart));
        when(productRepository.findById(product.getProductId())).thenReturn(java.util.Optional.of(product));
        when(cartMapper.toCartDto(cart)).thenReturn(new CartDto(cart.getCartId(), member.getMemberId(), new ArrayList<>(),totalPrice));

        CartDto result = cartService.addProductToCart(cart.getCartId(), product.getProductId(), 2L);

        verify(cartRepository).save(cart);
        assertEquals(cart.getCartId(), result.getCartId());
    }

    @Test
    @DisplayName("장바구니에서 상품 제거 테스트")
    void testRemoveProductFromCart() {
        // Given
        Long cartProductId = 1L; // ID 초기화
        CartProduct cartProduct = new CartProduct(cartProductId, cart, product, 2L);
        cart.getCartProducts().add(cartProduct); // 장바구니에 추가

        // Mock 설정
        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));

        // When
        cartService.removeProductFromCart(cart.getCartId(), cartProductId);

        // Then
        assertFalse(cart.getCartProducts().contains(cartProduct), "Cart should not contain the removed product");
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


