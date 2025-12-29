package com.qwarty.exception;

import com.qwarty.exception.type.AppException;
import com.qwarty.exception.type.FieldValidationException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ExceptionHttpStatusMapper exceptionHttpStatusMapper;

    /**
     * Default title for {@link #MethodArgumentNotValidException} and {@link #FieldValidationException}
     * Used by {@link #handleMethodArgumentNotValidExceptions()} and {@link #handleFieldValidationExceptions()}
     */
    private final String FIELD_VALIDATION_ERROR_TITLE = "Request validation failed";

    /**
     * Default title and detail for general exceptions.
     * Used by {@link #handleAllExceptions()}.
     */
    private final String INTERNAL_ERROR_TITLE = "Internal server error",
            INTERNAL_ERROR_DETAIL = "An unexpected error occurred. Please try again later.";

    /**
     * Handles {@link #MethodArgumentNotValidException}, thrown when a method argument
     * annotated with {@code @Valid} fails Jakarta Bean Validation checks.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidExceptions(
            MethodArgumentNotValidException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String title = FIELD_VALIDATION_ERROR_TITLE;
        List<Map<String, String>> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of("detail", String.format("%s %s", error.getField(), error.getDefaultMessage())))
                .toList();

        ProblemDetail responseBody = buildMultiErrorProblemDetail(exception, status, title, errors);
        return ResponseEntity.status(status).body(responseBody);
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ProblemDetail> handleFieldValidationExceptions(FieldValidationException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String title = FIELD_VALIDATION_ERROR_TITLE;
        List<Map<String, String>> errors = exception.getFieldErrors().stream()
                .map(error -> java.util.Map.of("detail", error.getMessage()))
                .toList();

        ProblemDetail responseBody = buildMultiErrorProblemDetail(exception, status, title, errors);
        return ResponseEntity.status(status).body(responseBody);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ProblemDetail> handleAppExceptions(AppException exception) {
        HttpStatus status = exception.getExceptionCode().getHttpStatus();
        String title = exception.getExceptionCode().getTitle();
        String detail = exception.getExceptionCode().getDetail();

        ProblemDetail responseBody = buildProblemDetail(exception, status, title, detail);
        return ResponseEntity.status(status).body(responseBody);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(Exception exception) {
        HttpStatus status = exceptionHttpStatusMapper.map(exception);
        String title = INTERNAL_ERROR_TITLE;
        String detail = INTERNAL_ERROR_DETAIL;

        ProblemDetail responseBody = buildProblemDetail(exception, status, title, detail);
        return ResponseEntity.status(status).body(responseBody);
    }

    /**
     * Creates an RFC 9457-compliant ProblemDetail to be returned by exception handlers and log the exception stack trace
     *
     * See: https://www.rfc-editor.org/rfc/rfc9457.html
     */
    private ProblemDetail buildProblemDetail(Exception exception, HttpStatus status, String title, String detail) {
        logger.warn("Exception occurred: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);

        return problemDetail;
    }

    /**
     * Builds a variant of ProblemDetail that contains multiple errors
     */
    private ProblemDetail buildMultiErrorProblemDetail(
            Exception exception, HttpStatus status, String title, List<Map<String, String>> errors) {
        logger.warn("Exception occurred: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(title);
        problemDetail.setProperty("errors", errors);

        return problemDetail;
    }
}
