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
    STUDENT_VERIFICATION_REQUIRED(HttpStatus.FORBIDDEN, 403, "재학생 인증이 필요합니다."),
    MEDIA_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 미디어 유형을 찾을 수 없습니다"),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 권한을 찾을 수 없습니다."),
    STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "해당 상태를 찾을 수 없습니다."),
    CLUB_DELETED(HttpStatus.GONE, 410, "삭제된 동아리입니다."),
    USER_DELETED(HttpStatus.GONE, 410, "삭제된 사용자입니다."),


    //파일 업로드 관련
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 500, "파일을 업로드할 수 없습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, 400, "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, 413, "파일 크기가 제한을 초과했습니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, 400, "빈 파일은 업로드할 수 없습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, 400, "유효하지 않은 파일명입니다."),
    FILE_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, 400, "업로드 가능한 파일 개수를 초과했습니다."),
    S3_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "파일 저장소 연결에 실패했습니다."),
    DUPLICATE_MEDIA_TYPE(HttpStatus.BAD_REQUEST, 400, "해당 타입의 이미지는 동아리만 하나만 등록 가능합니다."),



    //JWT
    JWT_ENTRY_POINT(HttpStatus.UNAUTHORIZED, 401, "인증되지 않은 사용자입니다."),
    JWT_ACCESS_DENIED(HttpStatus.FORBIDDEN, 403, "리소스에 접근할 권한이 없습니다."),
    JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, 401, "잘못된 JWT 서명입니다."),
    JWT_MALFORMED(HttpStatus.UNAUTHORIZED, 401, "유효하지 않은 JWT 토큰입니다."),
    JWT_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 401, "액세스 토큰이 만료되었습니다."),
    JWT_UNSUPPORTED(HttpStatus.UNAUTHORIZED, 401, "지원하지 않는 JWT 토큰입니다."),
    JWT_NOT_VALID(HttpStatus.UNAUTHORIZED, 401, "유효하지 않은 JWT 토큰입니다."),

    //HTTP 요청 관련
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, 405, "지원하지 않는 HTTP 메서드입니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, 400, "요청에 필요한 입력이 누락되었습니다."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, 400, "입력 형식이 올바르지 않습니다."),
    API_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "존재하지 않는 API 엔드포인트입니다."),
    
    //인증 관련
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, 401, "아이디 또는 비밀번호가 올바르지 않습니다."),
    PASSWORD_NOT_MATCHED(HttpStatus.UNAUTHORIZED, 401, "비밀번호가 일치하지 않습니다."),
    
    //데이터베이스 관련
    DATABASE_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "데이터베이스 연결에 실패했습니다."),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, 409, "데이터 무결성 제약 조건에 위반됩니다."),

    //질문 관련
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "질문을 찾을 수 없습니다."),
    CANNOT_UPDATE_ANSWERED_QUESTION(HttpStatus.BAD_REQUEST, 400, "답변이 채택된 질문은 수정할 수 없습니다."),
    ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "답변을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
}
