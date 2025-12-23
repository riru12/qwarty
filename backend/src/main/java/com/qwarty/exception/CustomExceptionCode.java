package com.qwarty.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CustomExceptionCode {
    USER_NOT_FOUND(10001, HttpStatus.NOT_FOUND, "User not found"),
    USERNAME_ALREADY_REGISTERED(10002, HttpStatus.CONFLICT, "Username already in use"),
    EMAIL_ALREADY_REGISTERED(10003, HttpStatus.CONFLICT, "Email already in use"),
    USER_NOT_VERIFIED(10004, HttpStatus.FORBIDDEN, "User is not verified. Check your email for verification"),
    REFRESH_TOKEN_MISSING(100005, HttpStatus.UNAUTHORIZED, "Refresh token is required"),
    REFRESH_TOKEN_INVALID(10006, HttpStatus.UNAUTHORIZED, "Refresh token is invalid"),
    REFRESH_TOKEN_EXPIRED(10007, HttpStatus.UNAUTHORIZED, "Refresh token has expired"),
    REFRESH_TOKEN_REVOKED(10008, HttpStatus.UNAUTHORIZED, "Refresh token has been revoked");

    private final int code;
    private final HttpStatus httpStatus;
    private final String messageTemplate;

    CustomExceptionCode(int code, HttpStatus httpStatus, String messageTemplate) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.messageTemplate = messageTemplate;
    }
}
