package com.uniclub.domain.auth.repository;

import com.uniclub.global.exception.CustomException;
import com.uniclub.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
public class INUAuthRepositoryImpl implements INUAuthRepository {

    // iOS 앱스토어 심사용 계정
    private static final String REVIEW_STUDENT_ID = "202012345";
    private static final String REVIEW_PASSWORD = "qwer123!";

    private final JdbcTemplate jdbcTemplate;

    public INUAuthRepositoryImpl(@Qualifier("oracleJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 학교 서버에 쿼리를 날리고, Y를 반환받으면 true
    public boolean verifySchoolLogin(String studentId, String password) {
        // 심사 계정은 학교 서버 검증 없이 통과
        if (REVIEW_STUDENT_ID.equals(studentId) && REVIEW_PASSWORD.equals(password)) {
            return true;
        }

        String sql = "SELECT F_LOGIN_CHECK(?,?) FROM DUAL";
        try{
            String result = jdbcTemplate.queryForObject(sql, String.class, studentId, password);
            return "Y".equals(result);
        } catch(DataAccessException e){
            throw new CustomException(ErrorCode.SCHOOL_SERVER_ERROR);
        }
    }
}
