package com.elice.holo.order.dto;

import com.elice.holo.order.domain.OrderStatus;
import lombok.Data;

@Data
public class UpdateOrderStatusRequestDto {
    private OrderStatus newStatus;
}