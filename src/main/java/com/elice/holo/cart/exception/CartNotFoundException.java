package com.elice.holo.cart.exception;


import com.elice.holo.common.exception.CustomException;
import com.elice.holo.common.exception.ErrorCode;

public class CartNotFoundException extends CustomException {

    public CartNotFoundException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }

}
