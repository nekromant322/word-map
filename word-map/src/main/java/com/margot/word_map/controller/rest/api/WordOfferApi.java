package com.margot.word_map.controller.rest.api;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.request.WordOfferChangeStatus;
import com.margot.word_map.dto.request.WordOffersSortRequest;
import com.margot.word_map.dto.response.WordOfferResponse;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(
        name = "WordsOfferController",
        description = "Контроллер для предложений слов пользователями и проверки админом",
        externalDocs = @ExternalDocumentation(
                description = "документация в Confluence",
                url = "документация для предложений слов еще не описана"
        )
)
@SecurityRequirement(name = "JWT")
public interface WordOfferApi {
    @Operation(
            summary = "Метод для предложения слова",
            externalDocs = @ExternalDocumentation(
                    description = "https://override-platform.atlassian.net/wiki/spaces/W/pages/209715201/POST+offer"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Слово предложено успешно"),
                    @ApiResponse(responseCode = "400", description = "Слово уже предложено"),
                    @ApiResponse(responseCode = "400", description = "Слово уже существует"),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен"),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации")
            }
    )
    void offerWord(@RequestBody CreateWordRequest word);

    @Operation(
            summary = "Метод для просмотра предложений",
            externalDocs = @ExternalDocumentation(
                    description =
                            "https://override-platform.atlassian.net/wiki/spaces/W/pages/207945769/POST+offer+list"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Предложения получены успешно"),
                    @ApiResponse(responseCode = "400", description = "Ошибка в параметрах", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации", content = @Content)
            }
    )
    Page<WordOfferResponse> getWordOffers(
            @RequestBody WordOffersSortRequest request);

    @Operation(
            summary = "Метод для изменения статусов слов",
            externalDocs = @ExternalDocumentation(
                    description =
                            "https://override-platform.atlassian.net/wiki/spaces/W/pages/209813505/POST+offer+status"
            )
    )
    void changeStatus(@RequestBody WordOfferChangeStatus status);

    @Operation(
            summary = "Метод для отказа предложения",
            externalDocs = @ExternalDocumentation(
                    description = "Метод еще не описан в Confluence"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Отклонено успешно"),
                    @ApiResponse(responseCode = "400", description = "Ошибка в параметрах"),
                    @ApiResponse(responseCode = "404", description = "Предложение не найдено"),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен"),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации")
            }
    )
    void rejectWord(@Parameter(description = "id предложения", example = "12")
                    @PathVariable Long id);

    @Operation(
            summary = "Метод для принятия предложения",
            externalDocs = @ExternalDocumentation(
                    description = "Метод еще не описан в Confluence"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Предложение принято успешно"),
                    @ApiResponse(responseCode = "400", description = "Ошибка в параметрах"),
                    @ApiResponse(responseCode = "400", description = "Слово уже существует"),
                    @ApiResponse(responseCode = "404", description = "Предложение не найдено"),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен"),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации")
            }
    )
    void approveWord(@Parameter(description = "id предложения", example = "12")
                     @PathVariable Long id,
                     @Parameter(description = "Описание слова", example = "Инструмент для ковки")
                     @RequestParam String description);
}
