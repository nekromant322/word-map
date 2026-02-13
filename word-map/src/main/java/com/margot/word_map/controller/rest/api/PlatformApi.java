package com.margot.word_map.controller.rest.api;

import com.margot.word_map.dto.PlatformDto;
import com.margot.word_map.dto.request.CreatePlatformRequest;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(
        name = "PlatformController",
        description = "Контроллер для работы с платформами"
)
@SecurityRequirement(name = "JWT")
@RequestMapping("/platform")
public interface PlatformApi {

    @Operation(
            summary = "Создание платформы",
            description = "Метод создания новой платформы для игрового мира",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/412352594/POST+platform"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Платформа создана"),
                    @ApiResponse(responseCode = "400", description = "Некорректный формат ввода"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
                    @ApiResponse(responseCode = "409", description = "Название уже существует")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    PlatformDto createPlatform(@Valid @RequestBody CreatePlatformRequest request);
}
