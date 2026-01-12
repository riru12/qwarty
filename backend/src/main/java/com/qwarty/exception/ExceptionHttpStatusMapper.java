package com.qwarty.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ExceptionHttpStatusMapper {

    public HttpStatus map(Exception ex) {

        return switch (ex) {
            case MethodArgumentNotValidException _, HttpMessageNotReadableException _ -> HttpStatus.BAD_REQUEST;

            case ResponseStatusException exception ->
                HttpStatus.valueOf(exception.getStatusCode().value());

            case AccessDeniedException _ -> HttpStatus.FORBIDDEN;

            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
