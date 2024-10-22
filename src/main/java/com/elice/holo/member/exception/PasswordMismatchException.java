package com.elice.holo.member.exception;

import com.elice.holo.common.exception.CustomException;
import com.elice.holo.common.exception.ErrorCode;

public class PasswordMismatchException extends CustomException {

    public PasswordMismatchException(String detailMessage) {
        super(ErrorCode.PASSWORD_MISMATCH, detailMessage);
    }
}