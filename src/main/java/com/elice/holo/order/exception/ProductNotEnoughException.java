package com.elice.holo.order.exception;

import com.elice.holo.common.exception.CustomException;
import com.elice.holo.common.exception.ErrorCode;

public class ProductNotEnoughException extends CustomException {

    public ProductNotEnoughException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }

}
