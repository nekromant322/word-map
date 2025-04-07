package com.margot.word_map.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

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
        )
)

public class SwaggerConfig {
    // Конфигурация для Swagger
}
