package com.margot.word_map.controller.rest.api;

import com.margot.word_map.dto.PatternDto;
import com.margot.word_map.dto.request.CreateUpdatePatternRequest;
import com.margot.word_map.dto.response.IdResponse;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "PatternController",
        description = "Контроллер для работы с паттернами"
)
@SecurityRequirement(name = "JWT")
public interface PatternApi {

    @Operation(
            summary = "Информации о паттерне",
            description = "Метод получения детальной информации о паттерне",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/380141569/GET+pattern+id"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Успех"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
                    @ApiResponse(responseCode = "404", description = "Паттерн не найден")
            }
    )
    PatternDto getPattern(Long id);

    @Operation(
            summary = "Создание паттерна",
            description = "Метод для создания паттерне",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/340525057/POST+pattern"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Паттерн создан"),
                    @ApiResponse(responseCode = "400", description = "Отсутствуют обязательные данные формы"),
                    @ApiResponse(responseCode = "400", description = "Некорректный формат ввода"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
            }
    )
    IdResponse<Long> createPattern(CreateUpdatePatternRequest request);

    @Operation(
            summary = "Обновление паттерна",
            description = "Метод для обновления паттерне",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/380338177/PUT+pattern+id"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Паттерн обновлен"),
                    @ApiResponse(responseCode = "400", description = "Отсутствуют обязательные данные формы"),
                    @ApiResponse(responseCode = "400", description = "Некорректный формат ввода"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
                    @ApiResponse(responseCode = "404", description = "Паттерн не найден")
            }
    )
    IdResponse<Long> updatePattern(Long id, CreateUpdatePatternRequest request);
}
