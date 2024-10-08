package com.elice.holo.common.exception;

public class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }

    // 기본 ErrorCode만 사용하는 생성자
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

    // error code 사용 및 메세지 설정 가능
    public CustomException(ErrorCode errorCode, String detailMessage) {
        super(String.format("%s - %s", errorCode.getMessage(), detailMessage));
    }
}
