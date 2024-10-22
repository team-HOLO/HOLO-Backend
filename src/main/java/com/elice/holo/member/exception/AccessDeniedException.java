package com.elice.holo.member.exception;

import com.elice.holo.common.exception.CustomException;
import com.elice.holo.common.exception.ErrorCode;

public class AccessDeniedException extends CustomException {

    public AccessDeniedException(String detailMessage) {
        super(ErrorCode.ACCESS_DENIED, detailMessage);
    }
}