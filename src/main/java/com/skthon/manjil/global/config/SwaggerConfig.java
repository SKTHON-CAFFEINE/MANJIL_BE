package com.skthon.manjil.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "🏋 만질만질 API 명세서",
            description =
                """
                <p><strong>맞춤 건강 운동 서비스, 만질만질</strong>은<br>
                만성질환 환자를 위한 안전하고 맞춤화된 운동을 제안하는 건강 파트너입니다.</p>
                """,
            contact =
                @Contact(name = "만질만질", url = "https://manjil.store", email = "1030n@naver.com")),
    security = @SecurityRequirement(name = "Authorization"),
    servers = {
      @Server(url = "https://api.manjil.store", description = "🚀 배포 서버"),
      @Server(url = "http://localhost:8080", description = "🛠️ 로컬 서버")
    })
@SecurityScheme(
    name = "Authorization",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT")
public class SwaggerConfig {

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("Swagger API") // API 그룹명
        .pathsToMatch("/api/**", "/swagger-ui/**", "/v3/api-docs/**")
        .build();
  }
}
