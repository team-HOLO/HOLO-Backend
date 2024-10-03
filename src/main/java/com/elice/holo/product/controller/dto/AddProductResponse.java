package com.elice.holo.product.controller.dto;

import com.elice.holo.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddProductResponse {

    private Long id;
    private String name;
    private int price;
    private String description;

    public AddProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescription();
    }

}
