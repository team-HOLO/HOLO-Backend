package com.elice.holo.common.exception;

import com.elice.holo.category.exception.CategoryNotFoundException;
import com.elice.holo.category.exception.DuplicateCategoryNameException;
import com.elice.holo.order.exception.OrderNotCancelableException;
import com.elice.holo.order.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Category가 존재하지 않는 경우에 대한 예외 처리
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(
        CategoryNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(ErrorCode.CATEGORY_NOT_FOUND.getStatus().value())
            .message(ex.getMessage())
            .build();

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    // 카테고리의 이름이 중복되는 경우에 대한 예외처리
    @ExceptionHandler(DuplicateCategoryNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateCategoryNameException(
        DuplicateCategoryNameException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.fromErrorCode(
            ErrorCode.DUPLICATE_CATEGORY_NAME);

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    // 주문 관련 예외 처리
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(ErrorCode.ORDER_NOT_FOUND.getStatus().value())
            .message(ex.getMessage())
            .build();

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler(OrderNotCancelableException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotCancelableException(
        OrderNotCancelableException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.fromErrorCode(
            ErrorCode.INVALID_REQUEST);

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    // Custom 예외를 제외한 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Internal Server Error", ex);
        ErrorResponse errorResponse = ErrorResponse.fromErrorCode(ErrorCode.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }
}
