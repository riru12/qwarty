package com.qwarty.exception.type;

import com.qwarty.exception.code.FieldValidationExceptionCode;
import java.util.List;
import lombok.Getter;

@Getter
public class FieldValidationException extends RuntimeException {

    private final List<FieldValidationExceptionCode> fieldErrors;

    public FieldValidationException(List<FieldValidationExceptionCode> fieldErrors) {
        super("One or more fields are invalid");
        this.fieldErrors = fieldErrors;
    }
}
