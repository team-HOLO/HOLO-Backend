package com.elice.holo.cart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.elice.holo.cart.Service.CartService;
import com.elice.holo.cart.controller.CartController;
import com.elice.holo.cart.dto.AddCartItemRequestDto;
import com.elice.holo.cart.dto.CartDto;
import com.elice.holo.member.domain.MemberDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class ControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartService cartService;

    @Mock
    private Authentication authentication;

    @Mock
    private MemberDetails memberDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @DisplayName("특정 회원의 장바구니 조회 테스트")
    void testGetCartByMemberId() {
        Long memberId = 1L;
        CartDto cartDto = new CartDto();
        when(authentication.isAuthenticated()).thenReturn(true);
        when(memberDetails.getMemberId()).thenReturn(memberId);
        when(authentication.getPrincipal()).thenReturn(memberDetails);
        when(cartService.getCartByMember(memberId)).thenReturn(cartDto);

        ResponseEntity<CartDto> response = cartController.getCartByMemberId(memberId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(cartDto, response.getBody());
        verify(cartService).getCartByMember(memberId);
    }


    @Test
    @DisplayName("장바구니에 상품 추가 테스트")
    void testAddCartItem() {
        AddCartItemRequestDto request = new AddCartItemRequestDto(1L, 1L, 2L, "red", "M");

        when(authentication.isAuthenticated()).thenReturn(true);
        when(memberDetails.getMemberId()).thenReturn(1L);
        when(authentication.getPrincipal()).thenReturn(memberDetails);

        ResponseEntity<Void> response = cartController.addCartItem(request);

        assertEquals(200, response.getStatusCodeValue());
        verify(cartService).addProductToCartV2(
            anyLong(),
            anyLong(),
            anyLong(),
            anyLong(),
            anyString(),
            anyString()
        );
    }

    @Test
    @DisplayName("장바구니 생성 테스트")
    void testCreateCart() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(memberDetails.getMemberId()).thenReturn(1L);
        when(authentication.getPrincipal()).thenReturn(memberDetails);

        CartDto newCart = new CartDto(); // 새 장바구니 객체 생성
        when(cartService.createCart(anyLong())).thenReturn(newCart);

        ResponseEntity<CartDto> response = cartController.createCart();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(newCart, response.getBody());
    }

    @Test
    @DisplayName("장바구니 전체 삭제 테스트")
    void testClearCart() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(memberDetails.getMemberId()).thenReturn(1L);
        when(authentication.getPrincipal()).thenReturn(memberDetails);

        ResponseEntity<Void> response = cartController.clearCart();

        assertEquals(204, response.getStatusCodeValue());
        verify(cartService).clearCart(anyLong());
    }

    @Test
    @DisplayName("장바구니 총 가격 조회 테스트")
    void testGetTotalPrice() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(memberDetails.getMemberId()).thenReturn(1L);
        when(authentication.getPrincipal()).thenReturn(memberDetails);

        CartDto cartDto = new CartDto(); // 총 가격을 계산할 장바구니 객체 생성
        when(cartService.calculateTotalPrice(anyLong())).thenReturn(cartDto);

        ResponseEntity<CartDto> response = cartController.getTotalPrice();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(cartDto, response.getBody());
    }
}


