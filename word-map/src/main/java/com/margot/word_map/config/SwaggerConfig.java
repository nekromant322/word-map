package com.margot.word_map.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "word-map backend",
                description = """
                        Бэкенд админки word-map.

                        Контакты:
                        - bahertylop — https://t.me/bahertylop
                        - Megokolos — https://t.me/Megokolos
                        """,
                version = "1.0.0"
        ),
        externalDocs = @ExternalDocumentation(
                description = "GitHub Repository",
                url = "https://github.com/nekromant322/word-map/tree/main"
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Server"),
                @Server(url = "http://word-map.ru", description = "Production Server")
        }
)
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                    .info(new io.swagger.v3.oas.models.info.Info()
                            .title("Word Map API")
                            .description("Swagger без авторизации")
                            .version("1.0"))
                .components(new io.swagger.v3.oas.models.Components())
                .addSecurityItem(new SecurityRequirement());
    }
}
