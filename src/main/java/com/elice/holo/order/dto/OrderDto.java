package com.elice.holo.order.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class OrderDto {

    private Long orderId;
    private Long memberId;
    private List<OrderProductDto> orderProducts = new ArrayList<>();
    ;
    private LocalDateTime orderDate;
    private String status;
}
