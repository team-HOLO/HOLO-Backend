//package com.elice.holo.cart.controller;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import com.elice.holo.cart.Service.CartService;
//import com.elice.holo.cart.dto.CartDto;
//import com.elice.holo.cart.dto.CartRequestDto;
//import com.elice.holo.member.domain.Member;
//import com.elice.holo.member.service.MemberService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.jayway.jsonpath.JsonPath;
//import java.util.Collections;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//class CartControllerTest {
//
//    @InjectMocks
//    private CartController cartController;
//
//    @Mock
//    private CartService cartService;
//
//    @Mock
//    private MemberService memberService;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private ObjectMapper objectMapper;
//
//    private Member member;
//    private CartDto mockCartDto;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//        this.mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
//
//        member = Member.builder()
//            .memberId(1L)
//            .email("test@example.com")
//            .password("password")
//            .name("Test User")
//            .age(30)
//            .gender(false)
//            .tel("123-456-7890")
//            .isDeleted(false)
//            .isAdmin(false)
//            .build();
//
//        mockCartDto = CartDto.builder()
//            .cartId(1L)
//            .totalPrice(100.0)
//            .products(Collections.emptyList())
//            .build();
//    }
//
//    @DisplayName("장바구니 조회 테스트")
//    @Test
//    void getCartByMemberIdTest() throws Exception {
//        when(memberService.getMemberEntityById(anyLong())).thenReturn(member);
//        when(cartService.getCartByMember(any(Member.class))).thenReturn(
//            mockCartDto);
//
//        mockMvc.perform(get("/api/cart/member/{memberId}", 1L))
//            .andExpect(status().isOk());
//    }
//
//    @DisplayName("장바구니 생성 테스트")
//    @Test
//    void createCartTest() throws Exception {
//        when(cartService.createCart()).thenReturn(mockCartDto);
//
//        mockMvc.perform(post("/api/cart"))
//            .andExpect(status().isCreated())
//            .andExpect(jsonPath("$.cartId").value(1L));
//    }
//
//    @DisplayName("장바구니 상품 추가 테스트")
//    @Test
//    void addProductToCartTest() throws Exception {
//        CartRequestDto cartRequestDto = new CartRequestDto(1L, 1, "red", "M");
//
//        when(cartService.addProductToCart(anyLong(), any(CartRequestDto.class))).thenReturn(
//            mockCartDto);
//
//        mockMvc.perform(post("/api/cart/{cartId}/products/{productId}", 1L, 1L)
//                .contentType("application/json")
//                .content(objectMapper.writeValueAsString(cartRequestDto)))
//            .andExpect(status().isCreated())
//            .andExpect(jsonPath("$.cartId").value(1L));
//    }
//
//    @DisplayName("장바구니 상품 수량 수정 테스트")
//    @Test
//    void updateProductQuantityTest() throws Exception {
//        when(cartService.updateProductQuantity(anyLong(), anyLong(), anyLong())).thenReturn(
//            mockCartDto);
//
//        mockMvc.perform(put("/api/cart/{cartId}/products/{cartProductId}", 1L, 1L)
//                .param("quantity", "2"))
//            .andExpect(status().isOk());
//    }
//
//    @DisplayName("장바구니 상품 제거 테스트")
//    @Test
//    void removeProductFromCartTest() throws Exception {
//        when(cartService.removeProductFromCart(anyLong(), anyLong())).thenReturn(mockCartDto);
//
//        mockMvc.perform(delete("/api/cart/{cartId}/products/{cartProductId}", 1L, 1L))
//            .andExpect(status().isOk());
//    }
//
//    @DisplayName("장바구니 전체 비우기 테스트")
//    @Test
//    void clearCartTest() throws Exception {
//        // 장바구니 생성
//        MvcResult result = mockMvc.perform(post("/api/cart/member/{memberId}", 1L))
//            .andExpect(status().isCreated())
//            .andReturn();
//
//        // 응답에서 cartId 추출
//        String content = result.getResponse().getContentAsString();
//        if (content == null || content.isEmpty()) {
//            throw new IllegalArgumentException("Response content cannot be null or empty");
//        }
//        Long cartId = JsonPath.parse(content).read("$.cartId", Long.class); // 응답에서 cartId 추출
//
//        // 장바구니에 상품 추가
//        mockMvc.perform(post("/api/cart/{cartId}/products/{productId}", cartId, 1L)
//                .param("quantity", "2"))
//            .andExpect(status().isCreated());
//
//        // 장바구니 비우기
//        mockMvc.perform(delete("/api/cart/{cartId}", cartId))
//            .andExpect(status().isNoContent());
//
//        // 장바구니 조회 및 products 확인
//        mockMvc.perform(get("/api/cart/member/{memberId}", 1L))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.cartProducts").isEmpty()); // products가 비어 있어야 함
//    }
//
//    @DisplayName("장바구니 총 가격 계산 테스트")
//    @Test
//    void calculateTotalPriceTest() throws Exception {
//        when(cartService.calculateTotalPrice(anyLong())).thenReturn(100.0);
//
//        mockMvc.perform(get("/api/cart/{cartId}/total", 1L))
//            .andExpect(status().isOk());
//    }
//}
