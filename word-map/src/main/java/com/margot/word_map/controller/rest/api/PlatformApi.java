package com.margot.word_map.controller.rest.api;

import com.margot.word_map.dto.OptionDto;
import com.margot.word_map.dto.PlatformDto;
import com.margot.word_map.dto.request.CreateUpdatePlatformRequest;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(
        name = "PlatformController",
        description = "Контроллер для работы с платформами"
)
@SecurityRequirement(name = "JWT")
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
    PlatformDto createPlatform(CreateUpdatePlatformRequest request);

    @Operation(
            summary = "Редактирование платформы",
            description = "Метод редактирования платформы для размещения игрового мира",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/412549121/PUT+platform+id"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Платформа обновлена"),
                    @ApiResponse(responseCode = "400", description = "Некорректный формат ввода"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
                    @ApiResponse(responseCode = "404", description = "Платформа не найдена"),
                    @ApiResponse(responseCode = "409", description = "Название уже существует")
            }
    )
    PlatformDto updatePlatform(Long id, CreateUpdatePlatformRequest request);

    @Operation(
            summary = "Список платформ",
            description = "Метод получения списка платформ с игровыми мирами",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/412057602/GET+platform+option"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Данные успешно получены"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден")
            }
    )
    List<OptionDto> getPlatformOptions();
}
