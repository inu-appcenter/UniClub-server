package com.uniclub.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_SORT_CONDITION(HttpStatus.BAD_REQUEST, 400, "유효하지 않은 정렬 기준입니다."),
    NOT_AGREED(HttpStatus.BAD_REQUEST, 400, "개인정보 약관에 동의해야 합니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 유저를 찾을 수 없습니다."),
    DUPLICATE_STUDENT_ID(HttpStatus.CONFLICT, 409, "이미 존재하는 계정입니다."),
    CLUB_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 동아리를 찾을 수 없습니다."),
    MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 동아리 권한을 찾을 수 없습니다."),
    INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, 403, "사용 권한이 없습니다."),
    DUPLICATE_CLUB_NAME(HttpStatus.CONFLICT, 409, "이미 존재하는 동아리입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 400, "잘못된 입력입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 카테고리를 찾을 수 없습니다."),
    DUPLICATE_CATEGORY_NAME(HttpStatus.CONFLICT, 409, "이미 존재하는 카테고리입니다."),
    NOTIFICATION_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 알림 타입을 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
}
