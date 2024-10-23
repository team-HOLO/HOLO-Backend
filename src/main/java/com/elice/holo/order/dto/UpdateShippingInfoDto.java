package com.elice.holo.order.dto;

import lombok.Data;

@Data
public class UpdateShippingInfoDto {

    private String shippingAddress;
    private String recipientName;
    private String shippingRequest;
}