package com.uniclub.global.util;

import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public class CustomLogger {

    // ERROR 레벨 로그
    public static ResponseEntity<ErrorResponse> errorLog(String message, ErrorCode errorCode, Exception e) {
        log.error("{}", message, e);
        return ErrorResponse.toResponseEntity(errorCode);
    }

    // WARN 레벨 로그
    public static ResponseEntity<ErrorResponse> warnLog(String message, ErrorCode errorCode) {
        log.warn("{}", message);
        return ErrorResponse.toResponseEntity(errorCode);
    }

}
