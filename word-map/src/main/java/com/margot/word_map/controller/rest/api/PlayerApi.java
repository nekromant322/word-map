package com.margot.word_map.controller.rest.api;

import com.margot.word_map.dto.response.PlayerDetailedResponse;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "PlatformController",
        description = "Контроллер для работы с игроками"
)
@SecurityRequirement(name = "JWT")
public interface PlayerApi {
    @Operation(
            summary = "Информация игрока",
            description = "Метод получения детальной информации об игроке.",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/452296708/GET+player+id"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
            }
    )
    PlayerDetailedResponse getDetailedPlayerInfo(Long id);
}
