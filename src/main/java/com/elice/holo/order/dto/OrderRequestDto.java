package com.elice.holo.order.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderRequestDto {

    private List<OrderProductRequestDto> products = new ArrayList<>();
    private String shippingAddress;
    private String recipientName;
    private String shippingRequest;

}