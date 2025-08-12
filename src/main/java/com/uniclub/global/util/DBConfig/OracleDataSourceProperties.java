package com.uniclub.global.util.DBConfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "oracle.datasource")
public class OracleDataSourceProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;

}
