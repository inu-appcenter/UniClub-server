package com.uniclub.global.exception;

import com.uniclub.global.util.CustomLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.core.exception.SdkClientException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 서비스계층 예외처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return CustomLogger.warnLog(e.getMessage(), e.getErrorCode());
    }

    // 유효성 검사 예외처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException e) {

        // 필드, 유효성 실패 메시지 map으로 만듦
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fe ->
                errors.put(fe.getField(), fe.getDefaultMessage())
        );

        log.warn("유효성 검증 실패 : {}", errors);
        return ErrorResponse.validationFailed(errors);
    }

    // HTTP 메서드 오류 예외처리
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return CustomLogger.warnLog("잘못된 HTTP 메소드 접근", ErrorCode.METHOD_NOT_ALLOWED);
    }

    // 필수 파라미터 누락 예외처리  
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException e) {
        return CustomLogger.warnLog("필수 요청 인자 누락", ErrorCode.MISSING_PARAMETER);
    }

    // 타입 불일치 예외처리
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return CustomLogger.warnLog("요청 인자 타입 불일치", ErrorCode.TYPE_MISMATCH);
    }

    // 존재하지 않는 API 엔드포인트 예외처리
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException e) {
        return CustomLogger.warnLog("존재하지 않는 엔드포인트 접근", ErrorCode.API_NOT_FOUND);
    }

    // 인증 실패 예외처리
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException e) {
        return CustomLogger.warnLog("로그인 실패", ErrorCode.BAD_CREDENTIALS);
    }

    // 데이터 무결성 제약 위반 예외처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return CustomLogger.errorLog("데이터 무결성 제약 위반 발생", ErrorCode.DATA_INTEGRITY_VIOLATION, e);
    }

    // 학교 데이터베이스 서버 연결 오류 예외처리
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException e) {
        return CustomLogger.errorLog("학교 데이터베이스 서버 연결 오류 발생", ErrorCode.SCHOOL_SERVER_ERROR, e);
    }

    // 데이터베이스 연결 오류 예외처리
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException e) {
        return CustomLogger.errorLog("데이터베이스 연결 오류 발생", ErrorCode.DATABASE_CONNECTION_ERROR, e);
    }

    // S3 서비스 자체에서 발생하는 오류 처리 (예: 권한 없음, 요청 거절, 버킷 없음 등)
    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<ErrorResponse> handleS3Exception(S3Exception e) {
        return CustomLogger.errorLog("S3 연결 오류 발생", ErrorCode.S3_CONNECTION_ERROR, e);
    }

    // AWS SDK 클라이언트 단에서 발생하는 오류 처리 (예: 네트워크 끊김 등 클라이언트 문제)
    @ExceptionHandler(SdkClientException.class)
    public ResponseEntity<ErrorResponse> handleSdkClientException(SdkClientException e) {
        return CustomLogger.errorLog("S3 연결 클라이언트 오류 발생", ErrorCode.S3_CONNECTION_ERROR, e);
    }
}
