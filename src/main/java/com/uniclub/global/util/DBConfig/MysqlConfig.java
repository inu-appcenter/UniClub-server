package com.uniclub.global.util.DBConfig;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement // 트랜잭션 처리를 위해 @Transactional 어노테이션이 작동하도록 설정
@EnableJpaRepositories( // JpaRepository를 사용하는 클래스들의 위치 지정
        basePackages = "com.uniclub",
        entityManagerFactoryRef = "mysqlEntityManagerFactory",
        transactionManagerRef = "mysqlTransactionManager"
)
@EntityScan(basePackages = "com.example.ingle")
public class MysqlConfig {

    // JpaProperties는 yml파일에서 spring.jpa로 시작하는 모든 설정을 자동으로 매핑해주는 클래스
    private final JpaProperties jpaProperties;

    @Bean
    public MysqlDataSourceProperties mysqlDataSourceProperties() {
        return new MysqlDataSourceProperties();
    }

    @Bean(name = "mysqlDataSource")
    @Primary
    public DataSource mysqlDataSource(MysqlDataSourceProperties props) {
        return DataSourceBuilder.create()
                .url(props.getUrl())
                .username(props.getUsername())
                .password(props.getPassword())
                .driverClassName(props.getDriverClassName())
                .type(HikariDataSource.class)
                .build();
    }

    // JPA가 엔티티를 관리하기 위한 팩토리
    // MySQL DataSource를 사용
    // 아래 패키지의 @Entity 클래스를 관리 대상에 포함
    @Bean(name = "mysqlEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("mysqlDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.uniclub")
                .persistenceUnit("mysql")
                .properties(jpaProperties.getProperties())
                .build();
    }

    // JPA에서 사용하는 트랜잭션 매니저
    // @Transactional 동작 시 이 트랜잭션 매니저를 기본으로 사용
    @Bean(name = "mysqlTransactionManager")
    @Primary
    public PlatformTransactionManager mysqlTransactionManager(
            @Qualifier("mysqlEntityManagerFactory") EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}