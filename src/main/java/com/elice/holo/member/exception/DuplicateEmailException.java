package com.elice.holo.member.exception;

import com.elice.holo.common.exception.CustomException;
import com.elice.holo.common.exception.ErrorCode;

public class DuplicateEmailException extends CustomException {

    public DuplicateEmailException(String detailMessage) {
        super(ErrorCode.DUPLICATE_EMAIL, detailMessage);
    }
}