package com.elice.holo.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elice.holo.cart.Service.CartService;
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
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartProductRepository cartProductRepository;

    private Member member;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = new Member();
        member.memberId = 1L; // 직접 필드 초기화

        cart = new Cart();
        cart.cartId = 1L; // 직접 필드 초기화
        cart.member = member; // 직접 필드 초기화

        product = new Product();
        product.productId = 1L; // 직접 필드 초기화
    }

    @Test
    @DisplayName("특정 회원의 장바구니 조회")
    void testGetCartByMember() {
        when(cartRepository.findByMember_MemberId(anyLong())).thenReturn(Optional.of(cart));
        when(cartMapper.toCartDto(cart)).thenReturn(
            new CartDto(cart.getCartId(), Collections.emptyList(), 0.0));

        CartDto result = cartService.getCartByMember(member.getMemberId());

        assertEquals(cart.getCartId(), result.getCartId());
        verify(cartRepository).findByMember_MemberId(member.getMemberId());
    }

    @Test
    @DisplayName("존재하지 않는 회원의 장바구니 조회 시 예외 발생")
    void testGetCartByMemberNotFound() {
        when(cartRepository.findByMember_MemberId(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomException.class,
            () -> cartService.getCartByMember(member.getMemberId()));
    }

    @Test
    @DisplayName("장바구니 생성")
    void testCreateCart() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(cartMapper.toCartDto(any(Cart.class))).thenReturn(
            new CartDto(cart.getCartId(), Collections.emptyList(), 0.0));

        CartDto result = cartService.createCart(member.getMemberId());

        assertEquals(cart.getCartId(), result.getCartId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 장바구니 생성 시 예외 발생")
    void testCreateCartMemberNotFound() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> cartService.createCart(member.getMemberId()));
    }

    @Test
    @DisplayName("장바구니에 상품 추가")
    void testAddProductToCart() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(cartMapper.toCartDto(cart)).thenReturn(
            new CartDto(cart.getCartId(), Collections.emptyList(), 0.0));

        CartDto result = cartService.addProductToCartV2(member.getMemberId(), cart.getCartId(),
            product.getProductId(), 2L, "red", "M");

        assertEquals(cart.getCartId(), result.getCartId());
        verify(cartProductRepository).save(any(CartProduct.class));
    }

    @Test
    @DisplayName("존재하지 않는 장바구니에 상품 추가 시 예외 발생")
    void testAddProductToCartCartNotFound() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomException.class,
            () -> cartService.addProductToCartV2(member.getMemberId(), cart.getCartId(),
                product.getProductId(), 2L, "red", "M"));
    }

    @Test
    @DisplayName("장바구니 비우기")
    void testClearCart() {
        when(cartRepository.findByMember_MemberId(anyLong())).thenReturn(Optional.of(cart));

        cartService.clearCart(member.getMemberId());

        verify(cartProductRepository).deleteAll(any());
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("존재하지 않는 장바구니 비우기 시 예외 발생")
    void testClearCartNotFound() {
        when(cartRepository.findByMember_MemberId(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> cartService.clearCart(member.getMemberId()));
    }

    @Test
    @DisplayName("장바구니 총 가격 계산")
    void testCalculateTotalPrice() {
        CartProduct cartProduct = new CartProduct(cart, product, 2L, "red", "M");
        when(cartRepository.findByMember_MemberId(anyLong())).thenReturn(Optional.of(cart));
        when(cartProductRepository.findByCart_CartId(cart.getCartId())).thenReturn(
            Collections.singletonList(cartProduct));
        when(cartMapper.toCartProductDtoList(any())).thenReturn(Collections.emptyList());

        CartDto result = cartService.calculateTotalPrice(member.getMemberId());

        assertEquals(cart.getCartId(), result.getCartId());
        assertEquals(0.0, result.getTotalPrice());
    }

    @Test
    @DisplayName("존재하지 않는 장바구니 총 가격 계산 시 예외 발생")
    void testCalculateTotalPriceNotFound() {
        when(cartRepository.findByMember_MemberId(anyLong())).thenReturn(Optional.empty());

        assertThrows(CustomException.class,
            () -> cartService.calculateTotalPrice(member.getMemberId()));
    }

}
