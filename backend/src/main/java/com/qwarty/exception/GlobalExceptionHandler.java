package com.qwarty.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptions(CustomException error) {
        ErrorResponse responseBody = new ErrorResponse(error.getExceptionCode().getCode(), error.getMessage());
        return ResponseEntity.status(error.getExceptionCode().getHttpStatus()).body(responseBody);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception error) {
        String message = error.getMessage() != null ? error.getMessage() : "Unexpected internal server error";
        ErrorResponse responseBody = new ErrorResponse(5000, message);
        return ResponseEntity.internalServerError().body(responseBody);
    }
}
