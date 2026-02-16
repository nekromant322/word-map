package com.margot.word_map.controller.rest.api;

import com.margot.word_map.dto.OptionDto;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(
        name = "RuleController",
        description = "Контроллер для получения информации о правах пользователей админ панели"
)
@SecurityRequirement(name = "JWT")
public interface RuleApi {

    @Operation(
            summary = "Список прав",
            description = "Метод получения списка доступов/правил пользователей административной панели",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/385515833/GET+rule+option"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список успешно получен",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = OptionDto.class))
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен",
                            content = @Content),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден", content = @Content),
            }
    )
    List<OptionDto> getRules();
}
