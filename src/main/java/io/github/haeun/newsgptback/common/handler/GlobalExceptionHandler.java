package io.github.haeun.newsgptback.common.handler;

import io.github.haeun.newsgptback.common.enums.ErrorCode;
import io.github.haeun.newsgptback.common.exception.CustomException;
import io.github.haeun.newsgptback.common.exception.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.warn("Handled CustomException: {} - {}", e.getErrorCode(), e.getMessageToShow());
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = new ErrorResponse(
                errorCode.getHttpStatus().value(),
                errorCode.name(),
                e.getMessageToShow()
        );
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled Exception", e);
        ErrorResponse response = new ErrorResponse(
                500,
                "INTERNAL_ERROR",
                "예상치 못한 서버 오류가 발생했습니다."
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}
