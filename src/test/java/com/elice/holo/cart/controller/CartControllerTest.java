package com.elice.holo.cart.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.elice.holo.cart.Service.CartService;
import com.elice.holo.cart.dto.CartDto;
import com.elice.holo.cart.dto.CartProductDto;
import com.elice.holo.member.domain.Member;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private CartController cartController;

    @MockBean
    private CartService cartService;

    private CartDto mockCartDto;


    @BeforeEach
    public void mockMvcSetUp(){
        mockCartDto = CartDto.builder()
            .cartId(1L)
            .memberId(1L)
            .totalPrice(100.0)
            .build();
    }
    @DisplayName("장바구니 조회 테스트")
    @Test
    void getCartByMemberIdTest() throws Exception {
        Mockito.when(cartService.getCartByMember(Mockito.any())).thenReturn(mockCartDto);

        mockMvc.perform(get("/api/cart/{cartId}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cartId").value(1L));
    }

    @DisplayName("장바구니 생성 테스트")
    @Test
    void createCartTest() throws Exception {
        Mockito.when(cartService.createCart(Mockito.any())).thenReturn(mockCartDto);

        mockMvc.perform(post("/api/cart/member/{memberId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockCartDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.cartId").value(1L));
    }

    @DisplayName("장바구니 상품 추가 테스트")
    @Test
    void addProductToCartTest() throws Exception {
        Mockito.when(cartService.addProductToCart(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(mockCartDto);

        mockMvc.perform(post("/api/cart/{cartId}/products/{productId}", 1L, 1L)
                .param("quantity", "2"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.cartId").value(1L));
    }

    @DisplayName("장바구니 상품 수량 수정 테스트")
    @Test
    void updateProductQuantityTest() throws Exception {
        Mockito.when(cartService.updateProductQuantity(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(mockCartDto);

        mockMvc.perform(put("/api/cart/{cartId}/products/{cartProductId}", 1L, 1L)
                .param("quantity", "3"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cartId").value(1L));
    }

    @DisplayName("장바구니 상품 제거 테스트")
    @Test
    void removeProductFromCartTest() throws Exception {
        Mockito.when(cartService.removeProductFromCart(Mockito.anyLong(), Mockito.anyLong()))
            .thenReturn(mockCartDto);

        mockMvc.perform(delete("/api/cart/{cartId}/products/{cartProductId}", 1L, 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.cartId").value(1L));
    }

    @DisplayName("장바구니 전체 비우기 테스트")
    @Test
    void clearCartTest() throws Exception {
        mockMvc.perform(delete("/api/cart/{cartId}", 1L))
            .andExpect(status().isNoContent());
    }

    @DisplayName("장바구니 총 가격 계산 테스트")
    @Test
    void calculateTotalPriceTest() throws Exception {
        Mockito.when(cartService.calculateTotalPrice(Mockito.anyLong())).thenReturn(100.0);

        mockMvc.perform(get("/api/cart/{cartId}/total", 1L))
            .andExpect(status().isOk())
            .andExpect(content().string("100.0"));
    }


}
