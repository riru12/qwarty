package com.qwarty.exception.code;

import lombok.Getter;

@Getter
public enum FieldValidationExceptionCode {
    USERNAME_ALREADY_REGISTERED("username.already.registered"),
    EMAIL_ALREADY_REGISTERED("email.already.registered");

    private final String message;

    FieldValidationExceptionCode(String message) {
        this.message = message;
    }
}
