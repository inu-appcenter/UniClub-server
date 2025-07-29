package com.uniclub.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.Map;


@Builder
@Getter

public class ErrorResponse {
    private Integer code;
    private String name;
    private String message;
    private Map<String, String> errors;

    //비즈니스 로직 예외처리
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .code(errorCode.getCode())
                        .name(errorCode.name())
                        .message(errorCode.getMessage())
                        .build()
                );
    }

    //유효성 검사 예외처리
    public static ResponseEntity<ErrorResponse> validationFailed(Map<String, String> errors) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .code(errorCode.getCode())
                        .name(errorCode.name())
                        .message(errorCode.getMessage())
                        .errors(errors)
                        .build());
    }
}
