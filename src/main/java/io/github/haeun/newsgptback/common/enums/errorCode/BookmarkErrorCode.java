package io.github.haeun.newsgptback.common.enums.errorCode;

import io.github.haeun.newsgptback.common.enums.errorCode.base.ErrorCodeBase;
import org.springframework.http.HttpStatus;

public enum BookmarkErrorCode implements ErrorCodeBase {
    DUPLICATE_BOOKMARK_GROUP_NAME(HttpStatus.BAD_REQUEST, "동일한 북마크 그룹명이 존재합니다."),
    MAX_BOOKMARK_GROUP_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "북마크는 10개까지만 생성 가능합니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 북마크입니다."),
    BOOKMARK_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 북마크 그룹을 찾을 수 없습니다."),
    BOOKMARK_GROUP_CANNOT_BE_MODIFIED(HttpStatus.FORBIDDEN, "기본 그룹은 수정할 수 없습니다."),
    BOOKMARK_GROUP_CANNOT_BE_DELETED(HttpStatus.FORBIDDEN, "기본 그룹은 삭제할 수 없습니다."),
    BOOKMARK_INVALID_BATCH_OPERATION(HttpStatus.BAD_REQUEST, "일부 북마크가 존재하지 않거나 접근 권한이 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    BookmarkErrorCode(HttpStatus status, String message) {
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
