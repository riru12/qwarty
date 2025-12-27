package com.qwarty.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppExceptionCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found", "The user with the given identifier does not exist."),
    USER_NOT_VERIFIED(
            HttpStatus.FORBIDDEN,
            "User is not verified",
            "The user has not verified their account. Check the email for verification instructions."),
    REFRESH_TOKEN_MISSING(
            HttpStatus.UNAUTHORIZED, "Refresh token is required", "A refresh token must be provided to continue."),
    REFRESH_TOKEN_INVALID(
            HttpStatus.UNAUTHORIZED, "Refresh token is invalid", "The provided refresh token is invalid."),
    REFRESH_TOKEN_EXPIRED(
            HttpStatus.UNAUTHORIZED,
            "Refresh token has expired",
            "The provided refresh token has expired and cannot be used."),
    REFRESH_TOKEN_REVOKED(
            HttpStatus.UNAUTHORIZED,
            "Refresh token has been revoked",
            "The provided refresh token has been revoked and is no longer valid.");

    private final HttpStatus httpStatus;
    private final String title;
    private final String detail;

    AppExceptionCode(HttpStatus httpStatus, String title, String detail) {
        this.httpStatus = httpStatus;
        this.title = title;
        this.detail = detail;
    }
}
