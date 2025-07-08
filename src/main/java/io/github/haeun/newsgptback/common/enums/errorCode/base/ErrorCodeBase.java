package io.github.haeun.newsgptback.common.enums.errorCode.base;

import org.springframework.http.HttpStatus;

public interface ErrorCodeBase {
    HttpStatus getHttpStatus();
    String getMessage();
}
