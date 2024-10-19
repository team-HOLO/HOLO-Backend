package com.elice.holo.product.exception;

import com.elice.holo.common.exception.CustomException;
import com.elice.holo.common.exception.ErrorCode;

public class InvalidFileExtensionException extends CustomException {

    public InvalidFileExtensionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
