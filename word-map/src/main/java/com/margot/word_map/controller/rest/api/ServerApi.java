package com.margot.word_map.controller.rest.api;

import com.margot.word_map.dto.request.CreateServerRequest;
import com.margot.word_map.dto.request.UpdateServerRequest;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "ServerController",
        description = "Контроллер для работы с серверами(Уникальные игровые миры)"
)
@SecurityRequirement(name = "JWT")
public interface ServerApi {

    @Operation(
            summary = "Метод для создания нового действующего сервера игрового мира",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/500826193/POST+server"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "400", description = "Некорректный формат ввода", content = @Content),
                    @ApiResponse(responseCode = "401",
                            description = "Токен доступа недействителен", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Платформа не найдена", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Язык не найден", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Действующий сервер уже существует",
                            content = @Content),
            }
    )
    void createServer(CreateServerRequest request);

    @Operation(
            summary = "Метод обновления данных о сервере.",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/500826203/PATCH+server+id"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "400", description = "Некорректный формат ввода", content = @Content),
                    @ApiResponse(responseCode = "401",
                            description = "Токен доступа недействителен", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден", content = @Content),
            }
    )
    void updateServerName(UpdateServerRequest request, Long id);
}
