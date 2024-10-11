package com.elice.holo.order.exception;

import com.elice.holo.common.exception.CustomException;
import com.elice.holo.common.exception.ErrorCode;

public class OrderNotCancelableException extends CustomException {

    public OrderNotCancelableException(ErrorCode errorCode, String detailMessage) {
        super(errorCode, detailMessage);
    }
}