package com.elice.holo.order.controller;

import com.elice.holo.order.dto.OrderResponseDto;
import com.elice.holo.order.dto.UpdateOrderStatusDto;
import com.elice.holo.order.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "관리자 전체 주문 내역 조회", description = "관리자가 모든 주문 내역을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "주문 내역 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrdersForAdmin() {
        List<OrderResponseDto> orders = orderService.getAllOrdersForAdmin();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "주문 상태 변경", description = "관리자가 특정 주문의 상태를 변경합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "주문 상태 변경 성공"),
        @ApiResponse(responseCode = "404", description = "주문 없음")
    })
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
        @Parameter(description = "주문 ID") @PathVariable Long orderId,
        @RequestBody UpdateOrderStatusDto request) {
        orderService.updateOrderStatus(orderId, request.getNewStatus());
        return ResponseEntity.ok().build();
    }
}
