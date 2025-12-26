package com.qwarty.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomExceptions(CustomException error) {
        ErrorResponse responseBody =
                buildErrorResponse(error, error.getExceptionCode().getCode(), error.getMessage());
        return ResponseEntity.status(error.getExceptionCode().getHttpStatus()).body(responseBody);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception error) {
        String message = error.getMessage() != null ? error.getMessage() : "Unexpected internal server error";
        ErrorResponse responseBody = buildErrorResponse(error, 5000, message);
        return ResponseEntity.internalServerError().body(responseBody);
    }

    /**
     * Creates an ErrorResponse object for use in exception handlers and logs the exception stack trace
     */
    private ErrorResponse buildErrorResponse(Exception error, int errorCode, String errorMessage) {
        logger.error("Exception occurred: ", error);
        ErrorResponse responseBody = new ErrorResponse(errorCode, errorMessage);
        return responseBody;
    }
}
