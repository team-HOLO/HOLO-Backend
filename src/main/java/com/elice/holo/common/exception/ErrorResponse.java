package com.elice.holo.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String message;

    public static ErrorResponse fromErrorCode(ErrorCode errorCode) {
        return ErrorResponse.builder()
            .status(errorCode.getStatus().value())
            .message(errorCode.getMessage())
            .build();
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(this.status);
    }
}