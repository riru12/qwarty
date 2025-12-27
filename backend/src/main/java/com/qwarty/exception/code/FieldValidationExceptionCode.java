package com.qwarty.exception.code;

import lombok.Getter;

@Getter
public enum FieldValidationExceptionCode {
    USERNAME_ALREADY_REGISTERED("username", "Username already in use"),
    EMAIL_ALREADY_REGISTERED("email", "Email already in use");

    private final String field;
    private final String message;

    FieldValidationExceptionCode(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
