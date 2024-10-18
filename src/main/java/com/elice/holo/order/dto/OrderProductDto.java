package com.elice.holo.order.dto;

import com.elice.holo.order.domain.OrderProduct;
import lombok.Data;

@Data
public class OrderProductDto {
    private Long productId;
    private String productName;
    private int quantity;
    private String color;
    private String size;

    public OrderProductDto(OrderProduct orderProduct) {
        this.productId = orderProduct.getProduct().getProductId();
        this.productName = orderProduct.getProduct().getName();
        this.quantity = orderProduct.getQuantity();
        this.color = orderProduct.getColor();
        this.size = orderProduct.getSize();
    }
}