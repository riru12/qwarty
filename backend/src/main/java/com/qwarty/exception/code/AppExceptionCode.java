package com.qwarty.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppExceptionCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user.not.found.title", "user.not.found.detail"),
    USER_NOT_VERIFIED(HttpStatus.FORBIDDEN, "user.not.verified.title", "user.not.verified.detail"),
    REFRESH_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "refresh.token.missing.title", "refresh.token.missing.detail"),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "refresh.token.invalid.title", "refresh.token.invalid.detail"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "refresh.token.expired.title", "refresh.token.expired.detail"),
    REFRESH_TOKEN_REVOKED(HttpStatus.UNAUTHORIZED, "refresh.token.revoked.title", "refresh.token.revoked.detail");

    private final HttpStatus httpStatus;
    private final String title;
    private final String detail;

    AppExceptionCode(HttpStatus httpStatus, String title, String detail) {
        this.httpStatus = httpStatus;
        this.title = title;
        this.detail = detail;
    }
}
