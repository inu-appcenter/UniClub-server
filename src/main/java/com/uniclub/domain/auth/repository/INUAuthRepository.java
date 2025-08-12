package com.uniclub.domain.auth.repository;

// 프로퍼티 설정에 따라 배포환경에서는 Impl, 로컬에서는 Mock을 사용하도록 인터페이스 사용
public interface INUAuthRepository {
    boolean verifySchoolLogin(String studentId, String password);
}
