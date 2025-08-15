package com.uniclub.global.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniclub.global.exception.ErrorCode;
import com.uniclub.global.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 403, 필터체인에서 권한 오류인 경우의 예외를 다루는 핸들러
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(ErrorCode.JWT_ACCESS_DENIED.getHttpStatus().value());
        // 응답의 콘텐츠 타입을 JSON 형식으로 지정
        response.setContentType("application/json;charset=UTF-8");

        // Java 객체를 JSON으로 바꿔주는 도구
        ObjectMapper objectMapper = new ObjectMapper();
        // 아래 객체를 직렬화
        String json = objectMapper.writeValueAsString(
                ErrorResponse.builder()
                        .code(ErrorCode.JWT_ACCESS_DENIED.getCode())
                        .name(ErrorCode.JWT_ACCESS_DENIED.name())
                        .message(ErrorCode.JWT_ACCESS_DENIED.getMessage())
                        .build()
        );
        // JSON을 HTTP 응답 본문에 작성
        response.getWriter().write(json);
    }
}