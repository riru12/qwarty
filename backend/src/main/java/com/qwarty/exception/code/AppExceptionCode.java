package com.qwarty.exception.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppExceptionCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user.not.found.title", "user.not.found.detail"),
    USER_NOT_VERIFIED(HttpStatus.FORBIDDEN, "user.not.verified.title", "user.not.verified.detail"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "user.invalid.credentials.title", "user.invalid.credentials.detail"),
    NO_SESSION(HttpStatus.UNAUTHORIZED, "session.not.found.title", "session.not.found.detail"),
    SESSION_USERNAME_NOT_FOUND(
            HttpStatus.UNAUTHORIZED, "session.username.not.found.title", "session.username.not.found.detail"),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "room.not.found.title", "room.not.found.detail"),
    ROOM_FULL(HttpStatus.CONFLICT, "room.full.title", "room.full.detail");

    private final HttpStatus httpStatus;
    private final String title;
    private final String detail;

    AppExceptionCode(HttpStatus httpStatus, String title, String detail) {
        this.httpStatus = httpStatus;
        this.title = title;
        this.detail = detail;
    }
}
