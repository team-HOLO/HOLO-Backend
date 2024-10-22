package com.elice.holo.common.exception;

import com.elice.holo.category.exception.CategoryNotFoundException;
import com.elice.holo.category.exception.DuplicateCategoryNameException;
import com.elice.holo.member.exception.AccessDeniedException;
import com.elice.holo.member.exception.DuplicateEmailException;
import com.elice.holo.member.exception.MemberNotFoundException;
import com.elice.holo.member.exception.PasswordMismatchException;
import com.elice.holo.order.exception.OrderNotCancelableException;
import com.elice.holo.order.exception.OrderNotFoundException;
import com.elice.holo.product.exception.DuplicateProductNameException;
import com.elice.holo.product.exception.InvalidFileExtensionException;
import com.elice.holo.product.exception.ProductNotFoundException;
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

    // AccessDeniedException 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("권한 오류: " + ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.fromErrorCode(ErrorCode.ACCESS_DENIED);

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(
        ProductNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.fromErrorCode(ErrorCode.PRODUCT_NOT_FOUND);

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler(DuplicateProductNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateProductNameException(
        DuplicateProductNameException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.fromErrorCode(ErrorCode.DUPLICATE_PRODUCT_NAME);

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    @ExceptionHandler(InvalidFileExtensionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileExtensionException(
        InvalidFileExtensionException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.fromErrorCode(ErrorCode.INVALID_FILE_EXTENSION);

        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    // 회원을 찾을 수 없는 경우에 대한 예외 처리
    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.fromErrorCode(ErrorCode.MEMBER_NOT_FOUND);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }

    // 이메일 중복 시 예외 처리
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmailException(DuplicateEmailException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.fromErrorCode(ErrorCode.DUPLICATE_EMAIL);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordMismatchException(PasswordMismatchException ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse errorResponse = ErrorResponse.fromErrorCode(ErrorCode.PASSWORD_MISMATCH);
        return new ResponseEntity<>(errorResponse, errorResponse.getHttpStatus());
    }
}
