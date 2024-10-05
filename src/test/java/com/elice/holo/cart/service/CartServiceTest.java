package com.elice.holo.cart.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elice.holo.cart.Service.CartService;
import com.elice.holo.cart.domain.Cart;
import com.elice.holo.cart.domain.CartProduct;
import com.elice.holo.cart.repository.CartRepository;
import com.elice.holo.member.domain.Member;
import com.elice.holo.product.domain.Product;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    private Member member;
    private Cart cart;
    private Product product;
    private CartProduct cartProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = new Member();
        member.setMemberId(1L);

        cart = Cart.createCart(member);

        product = Product.createProduct("Test product", 100, "Test Description", 10);

        cartProduct = new CartProduct(cart, product, 2L);
    }

    @Test
    void testGetCartByMember() {
        when(cartRepository.findByMember_MemberId(member.getMemberId())).thenReturn(cart);

        Cart result = cartService.getCartByMember(member);

        assertNotNull(result);
        assertEquals(cart, result);
        verify(cartRepository).findByMember_MemberId(member.getMemberId());
    }

    @Test
    void testCreateCart() {
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.createCart(member);

        assertNotNull(result);
        assertEquals(cart, result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testAddProductToCart() {
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.addProductToCart(cart, product, 2L);

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(captor.capture());
        Cart savedCart = captor.getValue();
        assertEquals(1, savedCart.getCartProducts().size());
        assertEquals(2L, savedCart.getCartProducts().get(0).getQuantity());
    }

    @Test
    void testRemoveProductFromCart() {
        cart.addCartPoduct(product, 2L);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.removeProductFromCart(cart, cartProduct);

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(captor.capture());
        Cart savedCart = captor.getValue();
        assertFalse(savedCart.getCartProducts().contains(cartProduct)); // 기대값을 false로 설정
    }

    @Test
    void testUpdateProductQuantity() {
        cart.addCartPoduct(product, 2L); // 장바구니에 상품 추가
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // 첫 번째 cartProduct를 가져오기
        CartProduct cartProductToUpdate = cart.getCartProducts().get(0);
        cartService.updateProductQuantity(cart, cartProductToUpdate, 5L);

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(captor.capture());
        Cart savedCart = captor.getValue();

        // 수량이 제대로 업데이트되었는지 확인
        assertEquals(5L, savedCart.getCartProducts().get(0).getQuantity());
    }

    @Test
    void testClearCart() {
        cart.addCartPoduct(product, 2L);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.clearCart(cart);

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(captor.capture());
        Cart savedCart = captor.getValue();
        assertTrue(savedCart.getCartProducts().isEmpty());
    }


    @Test
    void testRemoveSpecificProducts() {
        cart.addCartPoduct(product, 2L);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.removeSpecificProducts(cart, Arrays.asList(cartProduct));

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(captor.capture());
        Cart savedCart = captor.getValue();
        assertFalse(savedCart.getCartProducts().contains(cartProduct)); // 기대값을 false로 설정
    }

    @Test
    void testCalculateTotalPrice() {
        cart.addCartPoduct(product, 2L);
        double totalPrice = cartService.calculateTotalPrice(cart);
        assertEquals(200.0, totalPrice); // 100 * 2
    }


}
