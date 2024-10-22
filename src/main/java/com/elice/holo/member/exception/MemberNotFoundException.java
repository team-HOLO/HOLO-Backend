package com.elice.holo.member.exception;

import com.elice.holo.common.exception.CustomException;
import com.elice.holo.common.exception.ErrorCode;

public class MemberNotFoundException extends CustomException {

    public MemberNotFoundException(String detailMessage) {
        super(ErrorCode.MEMBER_NOT_FOUND, detailMessage);
    }
}