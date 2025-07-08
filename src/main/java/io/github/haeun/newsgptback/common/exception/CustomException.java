package io.github.haeun.newsgptback.common.exception;

import io.github.haeun.newsgptback.common.enums.errorCode.base.ErrorCodeBase;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCodeBase errorCode;
    private final String customMessage;

    public CustomException(ErrorCodeBase errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    public CustomException(ErrorCodeBase errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    public String getMessageToShow() {
        return customMessage != null ? customMessage : errorCode.getMessage();
    }
}