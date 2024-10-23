package com.elice.holo.order.controller;

import com.elice.holo.order.dto.OrderRequestDto;
import com.elice.holo.order.dto.OrderResponseDto;
import com.elice.holo.order.dto.UpdateShippingInfoDto;
import com.elice.holo.order.service.OrderService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    // 주문 생성
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderRequestDto orderRequest) {
        Long orderId = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }

    //회원이 주문내역 조회
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getMemberOrders() {
        List<OrderResponseDto> orders = orderService.getMemberOrders();
        return ResponseEntity.ok(orders);
    }

    //주문 취소 (ORDER 상태일때만 가능)
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    //주문 수정
    @PutMapping("/{orderId}/update")
    public ResponseEntity<Void> updateShippingInfo(@PathVariable Long orderId,
        @RequestBody UpdateShippingInfoDto updateShippingInfoDto) {
        orderService.updateShippingInfo(orderId, updateShippingInfoDto.getShippingAddress(),
            updateShippingInfoDto.getRecipientName(), updateShippingInfoDto.getShippingRequest());
        return ResponseEntity.noContent().build();
    }

    // 주문 삭제 (소프트 딜리트)
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

}