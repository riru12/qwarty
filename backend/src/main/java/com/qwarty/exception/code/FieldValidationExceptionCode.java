package com.qwarty.exception.code;

import lombok.Getter;

@Getter
public enum FieldValidationExceptionCode {
    USERNAME_ALREADY_REGISTERED("Username already in use"),
    EMAIL_ALREADY_REGISTERED("Email already in use");

    private final String message;

    FieldValidationExceptionCode(String message) {
        this.message = message;
    }
}
