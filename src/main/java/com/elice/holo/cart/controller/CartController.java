package com.elice.holo.cart.controller;

import com.elice.holo.cart.Service.CartService;
import com.elice.holo.cart.dto.AddCartItemRequestDto;
import com.elice.holo.cart.dto.CartDto;
import com.elice.holo.member.domain.MemberDetails;
import com.elice.holo.member.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final MemberService memberService;


    @GetMapping("/member/{memberId}")
    public ResponseEntity<CartDto> getCartByMemberId(@PathVariable Long memberId) {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("로그인 후 이용해주세요.");
        }

        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 요청된 memberId와 현재 로그인된 사용자 ID가 일치하는지 확인
        if (!memberDetails.getMemberId().equals(memberId)) {
            throw new AccessDeniedException("해당 회원의 장바구니를 조회할 권한이 없습니다.");
        }

        // 장바구니 조회
        CartDto cartDto = cartService.getCartByMember(memberDetails.getMemberId());
        return ResponseEntity.ok(cartDto);
    }


    //장바구니 담기*//
    @PostMapping
    public ResponseEntity<Void> addCartItem(@RequestBody AddCartItemRequestDto request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 로그인한 사용자가 해당 회원의 정보를 조회할 권한이 있는지 확인
        if (authentication != null && authentication.isAuthenticated()) {
            cartService.addProductToCartV2(
                memberDetails.getMemberId(),
                request.getCartId(),
                request.getProductId(),
                request.getQuantity(),
                request.getColor(),
                request.getSize());
            return ResponseEntity.ok().build();

        } else { //인증되지 않은 비회원의 경우
            throw new AccessDeniedException("로그인 후 이용해주세요.");
        }

    }

    // 장바구니 생성 메서드 추가
    @PostMapping("/create")
    public ResponseEntity<CartDto> createCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 로그인 여부 확인
        if (authentication != null && authentication.isAuthenticated()) {
            CartDto newCart = cartService.createCart(memberDetails.getMemberId());
            return ResponseEntity.ok(newCart); // 새로 생성된 장바구니 반환
        } else {
            throw new AccessDeniedException("로그인 후 이용해주세요.");
        }
    }

    //전체 삭제//
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        // 로그인 여부 확인
        if (authentication != null && authentication.isAuthenticated()) {
            cartService.clearCart(memberDetails.getMemberId());
            return ResponseEntity.noContent().build(); // 204 No Content 응답
        } else {
            throw new AccessDeniedException("로그인 후 이용해주세요.");
        }
    }

    //총가격//
    @GetMapping("/totalPrice")
    public ResponseEntity<CartDto> getTotalPrice() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        if (authentication != null && authentication.isAuthenticated()) {
            CartDto cartDto = cartService.calculateTotalPrice(memberDetails.getMemberId());
            return ResponseEntity.ok(cartDto);
        } else {
            throw new AccessDeniedException("로그인 후 이용해주세요.");
        }
    }

}

//    //수량 수정//
//    @PutMapping("/products/{cartProductId}")
//    public ResponseEntity<CartDto> updateProductQuantity(
//        @PathVariable Long cartProductId,
//        @RequestParam(name = "quantity") Long quantity) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
//
//        // 로그인 여부 확인
//        if (authentication != null && authentication.isAuthenticated()) {
//            // 수량 수정
//            CartDto updatedCart = cartService.updateProductQuantityInCart(
//                memberDetails.getMemberId(),
//                cartProductId,
//                quantity);
//
//            return ResponseEntity.ok(updatedCart); // 수정된 장바구니 반환
//        } else {
//            throw new AccessDeniedException("로그인 후 이용해주세요."); // 비로그인 사용자 처리
//        }
//    }

//장바구니 상품 제거
//    @DeleteMapping("/{cartId}/products/{cartProductId}")
//    public ResponseEntity<CartDto> removeProductFromCart(
//        @PathVariable Long cartId,
//        @PathVariable Long cartProductId) {
//        CartDto cartDto = cartService.removeProductFromCart(cartId, cartProductId);
//        return ResponseEntity.ok(cartDto);
//    }

//}
