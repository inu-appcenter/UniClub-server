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
    private final JdbcTemplate jdbcTemplate;

    public INUAuthRepositoryImpl(@Qualifier("oracleJdbc") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 학교 서버에 쿼리를 날리고, Y를 반환받으면 true
    public boolean verifySchoolLogin(String studentId, String password) {
        String sql = "SELECT F_LOGIN_CHECK(?,?) FROM DUAL";

        try{
            String result = jdbcTemplate.queryForObject(sql, String.class, studentId, password);
            return "Y".equals(result);
        } catch(DataAccessException e){
            throw new CustomException(ErrorCode.SCHOOL_SERVER_ERROR);
        }
    }
}
