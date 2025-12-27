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
     * Default title and detail for {@link #MethodArgumentNotValidException} and {@link #FieldValidationException}
     * Used by {@link #handleMethodArgumentNotValidExceptions()} and {@link #handleFieldValidationExceptions()}
     */
    private final String FIELD_VALIDATION_ERROR_TITLE = "Validation failed",
            FIELD_VALIDATION_ERROR_DETAIL = "One or more fields are invalid.";

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
        String detail = FIELD_VALIDATION_ERROR_DETAIL;
        List<Map<String, String>> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "message", error.getDefaultMessage()))
                .toList();

        ProblemDetail responseBody = buildProblemDetail(exception, status, title, detail, errors);
        return ResponseEntity.status(status).body(responseBody);
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ProblemDetail> handleFieldValidationExceptions(FieldValidationException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String title = FIELD_VALIDATION_ERROR_TITLE;
        String detail = FIELD_VALIDATION_ERROR_DETAIL;
        List<Map<String, String>> errors = exception.getFieldErrors().stream()
                .map(error -> java.util.Map.of(
                        "field", error.getField(),
                        "message", error.getMessage()))
                .toList();

        ProblemDetail responseBody = buildProblemDetail(exception, status, title, detail, errors);
        return ResponseEntity.status(status).body(responseBody);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ProblemDetail> handleAppExceptions(AppException exception) {
        HttpStatus status = exception.getExceptionCode().getHttpStatus();
        String title = exception.getExceptionCode().getTitle();
        String detail = exception.getExceptionCode().getDetail();

        ProblemDetail responseBody = buildProblemDetail(exception, status, title, detail, null);
        return ResponseEntity.status(status).body(responseBody);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAllExceptions(Exception exception) {
        HttpStatus status = exceptionHttpStatusMapper.map(exception);
        String title = INTERNAL_ERROR_TITLE;
        String detail = INTERNAL_ERROR_DETAIL;

        ProblemDetail responseBody = buildProblemDetail(exception, status, title, detail, null);
        return ResponseEntity.status(status).body(responseBody);
    }

    /**
     * Creates a RFC 9457-compliant ProblemDetail to be returned by exception handlers and log the exception stack trace
     *
     * @param errors an optional list of multiple errors (e.g., for validation failures);
     * each map should contain keys like "field" and "message"; can be null
     *
     * See: https://www.rfc-editor.org/rfc/rfc9457.html
     */
    private ProblemDetail buildProblemDetail(
            Exception exception, HttpStatus status, String title, String detail, List<Map<String, String>> errors) {
        logger.warn("Exception occurred: {} - {}", exception.getClass().getSimpleName(), exception.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);

        if (errors != null && !errors.isEmpty()) {
            problemDetail.setProperty("errors", errors);
        }
        return problemDetail;
    }
}
