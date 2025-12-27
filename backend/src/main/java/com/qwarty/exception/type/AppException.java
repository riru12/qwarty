package com.qwarty.exception.type;

import com.qwarty.exception.code.AppExceptionCode;
import java.text.MessageFormat;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final AppExceptionCode exceptionCode;
    private final Object[] messageArgs;

    public AppException(AppExceptionCode exceptionCode) {
        super(formatMessage(exceptionCode, null));
        this.exceptionCode = exceptionCode;
        this.messageArgs = null;
    }

    public AppException(AppExceptionCode exceptionCode, Object... messageArgs) {
        super(formatMessage(exceptionCode, messageArgs));
        this.exceptionCode = exceptionCode;
        this.messageArgs = messageArgs;
    }

    private static String formatMessage(AppExceptionCode code, Object[] args) {
        if (args == null || args.length == 0) {
            return code.getDetail();
        }
        return MessageFormat.format(code.getDetail(), args);
    }
}
