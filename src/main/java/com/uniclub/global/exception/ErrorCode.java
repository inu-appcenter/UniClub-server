package com.uniclub.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    CLUB_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 동아리를 찾을 수 없습니다."),
    DUPLICATE_CLUB_NAME(HttpStatus.CONFLICT, 409, "이미 존재하는 동아리입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 400, "잘못된 입력입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 카테고리를 찾을 수 없습니다."),
    DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, 409, "이미 존재하는 카테고리입니다.");


    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
}
