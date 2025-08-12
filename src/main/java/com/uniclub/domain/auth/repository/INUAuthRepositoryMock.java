package com.uniclub.domain.auth.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

// 로컬환경에서는 항상 true를 반환해 테스트를 하도록 하는 Mock 객체
@Repository
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "false", matchIfMissing = true)
public class INUAuthRepositoryMock implements INUAuthRepository {
    public boolean verifySchoolLogin(String studentId, String password) {
        return true;
    }
}
