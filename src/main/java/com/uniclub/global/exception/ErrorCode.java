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
    NOTIFICATION_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 알림 타입을 찾을 수 없습니다."),
    SCHOOL_SERVER_ERROR(HttpStatus.BAD_GATEWAY, 502, "학교 서버 응답에 문제가 발생했습니다."),

    //파일 업로드 관련
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 500, "파일을 업로드할 수 없습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, 400, "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, 413, "파일 크기가 제한을 초과했습니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, 400, "빈 파일은 업로드할 수 없습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, 400, "유효하지 않은 파일명입니다."),
    FILE_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, 400, "업로드 가능한 파일 개수를 초과했습니다."),
    S3_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "파일 저장소 연결에 실패했습니다."),
    DUPLICATE_MEDIA_TYPE(HttpStatus.BAD_REQUEST, 400, "해당 타입의 이미지는 동아리만 하나만 등록 가능합니다.");





    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
}
