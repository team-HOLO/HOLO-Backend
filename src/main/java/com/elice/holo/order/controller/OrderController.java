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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "주문 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderRequestDto orderRequest) {
        Long orderId = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderId);
    }

    @Operation(summary = "회원 주문 내역 조회", description = "로그인한 회원의 주문 내역을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "주문 내역 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getMemberOrders() {
        List<OrderResponseDto> orders = orderService.getMemberOrders();
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "주문 취소", description = "주문 상태가 ORDER일 때만 주문을 취소합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
        @ApiResponse(responseCode = "404", description = "주문 없음")
    })
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@Parameter(description = "주문 ID") @PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "주문 수정", description = "주문 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "주문 수정 성공"),
        @ApiResponse(responseCode = "404", description = "주문 없음")
    })
    @PutMapping("/{orderId}/update")
    public ResponseEntity<Void> updateShippingInfo(
        @Parameter(description = "주문 ID") @PathVariable Long orderId,
        @RequestBody UpdateShippingInfoDto updateShippingInfoDto) {
        orderService.updateShippingInfo(orderId, updateShippingInfoDto.getShippingAddress(),
            updateShippingInfoDto.getRecipientName(), updateShippingInfoDto.getShippingRequest());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "주문 삭제", description = "주문을 소프트 딜리트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "주문 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "주문 없음")
    })
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@Parameter(description = "주문 ID") @PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
