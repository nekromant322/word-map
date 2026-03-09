package com.margot.word_map.migration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class MigrationTest {

    static DockerImageName POSTGIS_IMAGE = DockerImageName
            .parse("postgis/postgis:15-3.5-alpine")
            .asCompatibleSubstituteFor("postgres");

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(POSTGIS_IMAGE);

    @Test
    @DisplayName("Схема бд в миграции совпадает с моделями в коде")
    void checkLiquibase() {
    }
}