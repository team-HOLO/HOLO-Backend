//package com.elice.holo.cart.service;
//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.elice.holo.cart.Service.CartService;
//import com.elice.holo.cart.domain.Cart;
//import com.elice.holo.cart.domain.CartProduct;
//import com.elice.holo.cart.dto.CartDto;
//import com.elice.holo.cart.dto.CartProductDto;
//import com.elice.holo.cart.mapper.CartMapper;
//import com.elice.holo.cart.repository.CartRepository;
//import com.elice.holo.member.domain.Member;
//import com.elice.holo.product.domain.Product;
//import com.elice.holo.product.repository.ProductRepository;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//
//
//class CartServiceTest {
//
//    @InjectMocks
//    private CartService cartService;
//
//    @Mock
//    private CartRepository cartRepository;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private CartMapper cartMapper;
//
//
////    @BeforeEach
////    void setUp() {
////        MockitoAnnotations.openMocks(this);
////
////
////    }
//
//
////    @DisplayName("특정 회원의 장바구니 조회 테스트")
////    @Test
////    void getCartByMemberTest() {
////        //Given
////        Member member = new Member();
////        member.setMemberId(1L);
////        Cart cart = Cart.createCart(member);
////
////        List<CartProductDto> productDtos = new ArrayList<>(); // Mock CartProductDtos
////        CartDto cartDto = new CartDto(cart.getCartId(), member.getMemberId(), productDtos);
////
////        when(cartRepository.findByMember_MemberId(member.getMemberId())).thenReturn(cart);
////        when(cartMapper.toCartDto(cart)).thenReturn(cartDto);
////
////        // When
////        CartDto result = cartService.getCartByMember(member);
////
////        //then
////        assertNotNull(result);
////        assertEquals(cart.getCartId(), result.getCartId());
////        assertEquals(member.getMemberId(), result.getMemberId());
////    }
////
////    @DisplayName("장바구니 생성 테스트")
////    @Test
////    void createCartTest() {
////        // Given
////        Member member = new Member();
////        member.setMemberId(1L);
////        Cart cart = Cart.createCart(member); // 회원으로 장바구니 생성
////
////
////        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
////        when(cartMapper.toCartDto(cart)).thenReturn(new CartDto(cart.getCartId(), member.getMemberId(), new ArrayList<>()));
////
////        // When: 실제 메서드 호출
////        CartDto result = cartService.createCart(member);
////
////        // Then: 결과 검증
////        assertNotNull(result);
////        assertEquals(cart.getCartId(), result.getCartId());
////    }
////
////}


