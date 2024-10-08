package com.elice.holo.order.controller;

import com.elice.holo.order.dto.OrderDto;
import com.elice.holo.order.service.OrderService;
import com.elice.holo.product.dto.AddProductResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 회원 주문내역 조회
    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrderList(@RequestParam String email) {
        List<OrderDto> orderList = orderService.getOrderList(email);
        return ResponseEntity.ok(orderList);
    }

    // 주문 생성
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDto orderDto,
        @RequestParam String email) {
        Long orderId = orderService.createOrder(orderDto, email);
        return new ResponseEntity<>(orderId, HttpStatus.CREATED);
    }

    // 주문 취소
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 배송지 수정
    @PutMapping("/{orderId}")
    public ResponseEntity<Void> updateOrder(@PathVariable Long orderId,
        @RequestBody OrderDto orderDto) {
        orderService.updateOrder(orderId, orderDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 상품 정보 조회
    @GetMapping("/products")
    public ResponseEntity<List<AddProductResponse>> getProductsInCart(
        @RequestParam List<Long> productIds) {
        List<AddProductResponse> products = orderService.getProductsId(productIds);
        return ResponseEntity.ok(products);
    }

    // 관리자 페이지에서 모든 회원들의 주문내역 조회
    @GetMapping("/admin/orders")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orderList = orderService.getAllOrders();
        return ResponseEntity.ok(orderList);
    }

    // 관리자에 의한 사용자의 주문정보 삭제
    @DeleteMapping("/admin/orders/{orderId}")
    public ResponseEntity<Void> deleteOrderByAdmin(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}