package com.uniclub.global.util.DBConfig;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "oracle.enabled", havingValue = "true")
@EnableConfigurationProperties(OracleDataSourceProperties.class)
public class OracleConfig {

    // oracle Datasource 빈 등록
    @Bean(name = "oracleDataSource")
    public DataSource oracleDataSource(OracleDataSourceProperties props) {
        return DataSourceBuilder.create()
                .url(props.getUrl())
                .username(props.getUsername())
                .password(props.getPassword())
                .driverClassName(props.getDriverClassName())
                .type(HikariDataSource.class).build();
    }

    // oracle Datasource 빈을 주입받아 JdbcTemplate 객체 생성
    @Bean(name = "oracleJdbc")
    public JdbcTemplate oracleJdbcTemplate(@Qualifier("oracleDataSource") DataSource oracleDataSource) {
        return new JdbcTemplate(oracleDataSource);
    }
}
