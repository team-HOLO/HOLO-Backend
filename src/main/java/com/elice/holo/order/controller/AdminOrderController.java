package com.elice.holo.order.controller;

import com.elice.holo.order.dto.OrderResponseDto;
import com.elice.holo.order.dto.UpdateOrderStatusRequestDto;
import com.elice.holo.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    // 관리자 전체 주문내역 조회
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrdersForAdmin() {
        List<OrderResponseDto> orders = orderService.getAllOrdersForAdmin();
        return ResponseEntity.ok(orders);
    }

    // 관리자 주문 취소
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> adminCancelOrder(@PathVariable Long orderId) {
        orderService.adminCancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    // 관리자가 주문 상태를 변경하는 기능
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId, @RequestBody UpdateOrderStatusRequestDto request) {
        orderService.updateOrderStatus(orderId, request.getNewStatus());
        return ResponseEntity.ok().build();
    }
}