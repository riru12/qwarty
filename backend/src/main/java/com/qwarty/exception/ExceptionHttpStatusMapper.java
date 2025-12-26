package com.qwarty.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ExceptionHttpStatusMapper {

    public HttpStatus map(Exception ex) {

        if (ex instanceof MethodArgumentNotValidException) {
            return HttpStatus.BAD_REQUEST;
        }

        if (ex instanceof HttpMessageNotReadableException) {
            return HttpStatus.BAD_REQUEST;
        }

        if (ex instanceof MissingRequestCookieException) {
            return HttpStatus.BAD_REQUEST;
        }

        if (ex instanceof ResponseStatusException exception) {
            return HttpStatus.valueOf(exception.getStatusCode().value());
        }

        if (ex instanceof AccessDeniedException) {
            return HttpStatus.FORBIDDEN;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
