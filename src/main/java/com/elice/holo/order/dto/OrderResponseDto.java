package com.elice.holo.order.dto;

import com.elice.holo.order.domain.Order;
import com.elice.holo.order.domain.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;


@Data
public class OrderResponseDto {

    private Long orderId;
    private String memberName;
    private int totalPrice;
    private List<OrderProductDto> orderProducts;
    private String shippingAddress;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private String recipientName;
    private String shippingRequest;

    public OrderResponseDto(Order order) {
        this.orderId = order.getOrderId();
        this.memberName = order.getMember().getName(); // Assuming Member has a getName() method
        this.totalPrice = order.getTotalPrice();
        this.orderProducts = order.getOrderProducts().stream()
            .map(OrderProductDto::new)
            .collect(Collectors.toList());
        this.shippingAddress = order.getShippingAddress();
        this.status = order.getStatus();
        this.orderDate = order.getOrderDate();
        this.recipientName = order.getRecipientName();
        this.shippingRequest = order.getShippingRequest();

    }
}