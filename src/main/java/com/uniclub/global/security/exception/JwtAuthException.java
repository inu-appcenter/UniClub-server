package com.uniclub.global.security.exception;

import com.uniclub.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

// 필터체인에서 발생하는 커스텀 예외
@Getter
public class JwtAuthException extends AuthenticationException {
    private final ErrorCode errorCode;

    public JwtAuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
