package com.margot.word_map.controller.rest.api;

import com.margot.word_map.dto.request.CreateLeaderboardRequest;
import com.margot.word_map.dto.response.LeaderboardResponse;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "LeaderboardController",
        description = "Методы для работы с таблицой лидеров"
)
@SecurityRequirement(name = "JWT")
public interface LeaderboardApi {
    @Operation(
            summary = "Список лидербордов",
            description = "Метод получения полного списка лидербордов для админ-панели.",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/419495937/POST+leaderboard"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
            }
    )
    LeaderboardResponse getLeaderboardList(CreateLeaderboardRequest request);
}
