package com.elice.holo.order.dto;

import com.elice.holo.order.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class OrderDto {

    private Long orderId;
    private Long memberId;
    private List<OrderProductDto> orderProducts = new ArrayList<>();
    private String shippingAddress;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalPrice;
}
