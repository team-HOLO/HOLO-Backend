package com.elice.holo.category.exception;

import com.elice.holo.common.exception.CustomException;
import com.elice.holo.common.exception.ErrorCode;

public class DuplicateCategoryNameException extends CustomException {
    
    public DuplicateCategoryNameException(ErrorCode errorCode) {
        super(errorCode);
    }

}
