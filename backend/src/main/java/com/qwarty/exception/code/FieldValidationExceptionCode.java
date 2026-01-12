package com.qwarty.exception.code;

import lombok.Getter;

@Getter
public enum FieldValidationExceptionCode {
    USERNAME_ALREADY_REGISTERED("username", "username.already.registered"),
    EMAIL_ALREADY_REGISTERED("email", "email.already.registered");

    private final String field;
    private final String message;

    FieldValidationExceptionCode(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
