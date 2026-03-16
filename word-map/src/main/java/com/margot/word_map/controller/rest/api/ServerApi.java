package com.margot.word_map.controller.rest.api;

import com.margot.word_map.dto.request.CreateServerRequest;
import com.margot.word_map.dto.request.ListServerRequest;
import com.margot.word_map.dto.request.UpdateServerRequest;
import com.margot.word_map.dto.response.DeleteServerResponse;
import com.margot.word_map.dto.response.ListServerResponse;
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

    @Operation(
            summary = "Метод закрытия сервера.",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/502497302/DELETE+server+id+close"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "401",
                            description = "Токен доступа недействителен", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Сервер не найден", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Некорректные условия", content = @Content),
            }
    )
    void closeServer(Long id);

    @Operation(
            summary = "Метод частичной очистки данных сервера для снижения нагрузки на сервер.",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/500826213/DELETE+server+id+wipe"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "401",
                            description = "Токен доступа недействителен", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Сервер не найден", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Некорректные условия", content = @Content),
            }
    )
    void wipeServer(Long id);

    @Operation(
            summary = "Метод удаление сервера из реестра.",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/502497292/DELETE+server+id"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "401",
                            description = "Токен доступа недействителен", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Сервер не найден", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Некорректные условия", content = @Content),
                    @ApiResponse(responseCode = "409", description = "Выполняется очистка", content = @Content),
            }
    )
    DeleteServerResponse deleteServer(Long id);

    @Operation(
            summary = "Метод получения списка существующих серверов.",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/500826223/POST+server+list"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "401",
                            description = "Токен доступа недействителен", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден", content = @Content),
            }
    )
    ListServerResponse getServers(Integer page, Integer size, ListServerRequest request);
}
