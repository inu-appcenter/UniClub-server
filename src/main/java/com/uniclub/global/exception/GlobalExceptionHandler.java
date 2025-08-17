package com.uniclub.global.exception;

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

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 서비스계층 예외처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return ErrorResponse.toResponseEntity(e.getErrorCode());
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

        return ErrorResponse.validationFailed(errors);
    }

    // HTTP 메서드 오류 예외처리
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return ErrorResponse.toResponseEntity(ErrorCode.METHOD_NOT_ALLOWED);
    }

    // 필수 파라미터 누락 예외처리  
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException e) {
        return ErrorResponse.toResponseEntity(ErrorCode.MISSING_PARAMETER);
    }

    // 타입 불일치 예외처리
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ErrorResponse.toResponseEntity(ErrorCode.TYPE_MISMATCH);
    }

    // 존재하지 않는 API 엔드포인트 예외처리
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException e) {
        return ErrorResponse.toResponseEntity(ErrorCode.API_NOT_FOUND);
    }

    // 인증 실패 예외처리
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException e) {
        return ErrorResponse.toResponseEntity(ErrorCode.BAD_CREDENTIALS);
    }

    // 데이터 무결성 제약 위반 예외처리
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return ErrorResponse.toResponseEntity(ErrorCode.DATA_INTEGRITY_VIOLATION);
    }

    // 학교 데이터베이스 서버 연결 오류 예외처리
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErrorResponse> handleSQLException(SQLException e) {
        return ErrorResponse.toResponseEntity(ErrorCode.DATABASE_CONNECTION_ERROR);
    }

    // 데이터베이스 연결 오류 예외처리
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException e) {
        return ErrorResponse.toResponseEntity(ErrorCode.DATABASE_CONNECTION_ERROR);
    }

    // S3 서비스 자체에서 발생하는 오류 처리 (예: 권한 없음, 요청 거절, 버킷 없음 등)
    @ExceptionHandler(S3Exception.class)
    public ResponseEntity<ErrorResponse> handleS3Exception(S3Exception e) {
        return ErrorResponse.toResponseEntity(ErrorCode.S3_CONNECTION_ERROR);
    }

    // AWS SDK 클라이언트 단에서 발생하는 오류 처리 (예: 네트워크 끊김 등 클라이언트 문제)
    @ExceptionHandler(SdkClientException.class)
    public ResponseEntity<ErrorResponse> handleSdkClientException(SdkClientException e) {
        return ErrorResponse.toResponseEntity(ErrorCode.S3_CONNECTION_ERROR);
    }

}
