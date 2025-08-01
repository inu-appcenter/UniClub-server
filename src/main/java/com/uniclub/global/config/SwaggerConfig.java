package com.uniclub.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Value("${swagger.server-url}")
    private String serverUrl;

    @Bean
    public OpenAPI openAPI() {
        Server server = new Server();
        server.url(serverUrl);

        return new OpenAPI()
                .addServersItem(server)
                .components(new Components()
                        // JWT 보안 스키마 추가
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")))
                // 전역 보안 요구사항 추가
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("UniClub API")
                .description("UniClub REST API Documentation")
                .version("1.0.0");
    }
}
