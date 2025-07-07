package io.github.haeun.newsgptback.common.handler;

import io.github.haeun.newsgptback.common.enums.ErrorCode;
import io.github.haeun.newsgptback.common.exception.CustomException;
import io.github.haeun.newsgptback.common.exception.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.StandardCharsets;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletResponse servletResponse) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = new ErrorResponse(
                errorCode.getHttpStatus().value(),
                errorCode.name(),
                e.getMessageToShow()
        );
        servletResponse.setStatus(errorCode.getHttpStatus().value());
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(HttpServletResponse servletResponse) {
        ErrorResponse response = new ErrorResponse(
                500,
                "INTERNAL_ERROR",
                "예상치 못한 서버 오류가 발생했습니다."
        );
        servletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8))
                .body(response);
    }
}
