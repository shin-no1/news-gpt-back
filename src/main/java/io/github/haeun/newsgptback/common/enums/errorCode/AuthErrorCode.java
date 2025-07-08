package io.github.haeun.newsgptback.common.enums.errorCode;

import io.github.haeun.newsgptback.common.enums.errorCode.base.ErrorCodeBase;
import org.springframework.http.HttpStatus;

public enum AuthErrorCode implements ErrorCodeBase {
    // 회원가입/이메일 인증
    EMAIL_DOMAIN_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "naver.com 또는 kakao.com 이메일만 허용됩니다."),
    EMAIL_CODE_INVALID(HttpStatus.BAD_REQUEST, "인증번호가 올바르지 않거나 만료되었습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),

    // 인증 및 로그인
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자입니다."),
    LOGIN_LOCKED(HttpStatus.TOO_MANY_REQUESTS, "로그인 시도 횟수가 너무 많습니다. 잠시 후 다시 시도해주세요."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    AuthErrorCode(HttpStatus status, String message) {
        this.httpStatus = status;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
