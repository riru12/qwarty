package com.qwarty.exception;

import java.text.MessageFormat;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final CustomExceptionCode exceptionCode;
    private final Object[] messageArgs;

    public CustomException(CustomExceptionCode exceptionCode) {
        super(formatMessage(exceptionCode, null));
        this.exceptionCode = exceptionCode;
        this.messageArgs = null;
    }

    public CustomException(CustomExceptionCode exceptionCode, Object... messageArgs) {
        super(formatMessage(exceptionCode, messageArgs));
        this.exceptionCode = exceptionCode;
        this.messageArgs = messageArgs;
    }

    private static String formatMessage(CustomExceptionCode code, Object[] args) {
        if (args == null || args.length == 0) {
            return code.getMessageTemplate();
        }
        return MessageFormat.format(code.getMessageTemplate(), args);
    }
}
