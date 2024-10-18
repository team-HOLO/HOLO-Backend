package com.elice.holo.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found"),
    DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, "Duplicate category name"),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order not found"),
    ORDER_NOT_CANCELABLE(HttpStatus.BAD_REQUEST, "Order not cancelable"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access is denied"),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart not found"),
    CART_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart product not found");


    private final HttpStatus status;
    private final String message;


}
