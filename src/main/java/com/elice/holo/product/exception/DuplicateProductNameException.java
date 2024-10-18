package com.elice.holo.product.exception;

import com.elice.holo.common.exception.CustomException;
import com.elice.holo.common.exception.ErrorCode;

public class DuplicateProductNameException extends CustomException {

    public DuplicateProductNameException(ErrorCode errorCode) {
        super(errorCode);
    }
}
