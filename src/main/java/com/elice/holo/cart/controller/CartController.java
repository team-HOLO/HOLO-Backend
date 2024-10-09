package com.elice.holo.cart.controller;

import com.elice.holo.cart.Service.CartService;
import com.elice.holo.cart.domain.Cart;
import com.elice.holo.cart.dto.CartDto;
import com.elice.holo.member.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;


    public CartController(CartService cartService) {

        this.cartService = cartService;
    }

    //특정 회원의 장바구니 조회
    @GetMapping("/member/{memberId}")
    public ResponseEntity<CartDto>getCartByMemberId(@PathVariable Long memberId){
        Member member = new Member();
        CartDto cartDto = cartService.getCartByMember(member);
        return  new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    //장바구니 생성
    @PostMapping("/member/{memberId}")
    public ResponseEntity<CartDto>createCart(@PathVariable Long memberId){
        Member member = new Member();
        CartDto cartDto = cartService.createCart(member);
        return new ResponseEntity<>(cartDto, HttpStatus.CREATED);
    }

    //장바구니 상품 추가
    @PostMapping("/{cartId}/products/{productId}")
    public ResponseEntity<CartDto> addProductToCart(
        @PathVariable Long cartId,
        @PathVariable Long productId,
        @RequestParam Long quantity) {
        CartDto cartDto = cartService.addProductToCart(cartId, productId, quantity); // 상품 추가
        return new ResponseEntity<>(cartDto, HttpStatus.CREATED); // 생성 결과 반환
    }
    //장바구니 상품 수량 수정
    @PutMapping("/{cartId}/products/{cartProductId}")
    public ResponseEntity<CartDto> updateProductQuantity(
        @PathVariable Long cartId,
        @PathVariable Long cartProductId,
        @RequestParam Long quantity) {
        CartDto cartDto = cartService.updateProductQuantity(cartId, cartProductId, quantity); // 수량 수정
        return new ResponseEntity<>(cartDto, HttpStatus.OK); // 수정 결과 반환
    }

    //장바구니 상품 제거
    @DeleteMapping("/{cartId}/products/{cartProductId}")
    public ResponseEntity<CartDto> removeProductFromCart(
        @PathVariable Long cartId,
        @PathVariable Long cartProductId){
        CartDto cartDto = cartService.removeProductFromCart(cartId,cartProductId);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    //장바구니 전체 비우기
    @DeleteMapping("/{cartId}")
    public ResponseEntity<CartDto> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId); // 장바구니 비우기
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 비움 결과 반환
    }

    //장바구니 총 가격 계산
    @GetMapping("/{cartId}/total")
    public ResponseEntity<Double> calculateTotalPrice(@PathVariable Long cartId) {
        double totalPrice = cartService.calculateTotalPrice(cartId); // 총 가격 계산
        return new ResponseEntity<>(totalPrice, HttpStatus.OK); // 가격 반환
    }
}
