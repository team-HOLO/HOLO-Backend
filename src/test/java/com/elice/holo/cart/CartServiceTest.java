//package com.elice.holo.cart;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.elice.holo.cart.Service.CartService;
//import com.elice.holo.cart.domain.Cart;
//import com.elice.holo.cart.domain.CartProduct;
//import com.elice.holo.cart.dto.CartDto;
//import com.elice.holo.cart.mapper.CartMapper;
//import com.elice.holo.cart.repository.CartProductRepository;
//import com.elice.holo.cart.repository.CartRepository;
//import com.elice.holo.common.exception.CustomException;
//import com.elice.holo.member.domain.Member;
//import com.elice.holo.member.repository.MemberRepository;
//import com.elice.holo.product.domain.Product;
//import com.elice.holo.product.repository.ProductRepository;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
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
//    private CartMapper cartMapper;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private CartProductRepository cartProductRepository;
//
//    private Member member;
//    private Product product;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        member = new Member(); // 생성자를 통해 초기화
//        product = new Product(); // 생성자를 통해 초기화
//    }
//
//
//}
