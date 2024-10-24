package com.elice.holo.order.exception;

import com.elice.holo.common.exception.CustomException;
import com.elice.holo.common.exception.ErrorCode;

public class DiscordMessageFailedException extends CustomException {

    public DiscordMessageFailedException(ErrorCode errorCode,
        String detailMessage) {
        super(errorCode, detailMessage);
    }
}
