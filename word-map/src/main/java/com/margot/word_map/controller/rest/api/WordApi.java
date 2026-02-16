package com.margot.word_map.controller.rest.api;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.request.DictionaryListRequest;
import com.margot.word_map.dto.request.UpdateWordRequest;
import com.margot.word_map.dto.response.DictionaryDetailedWordResponse;
import com.margot.word_map.dto.response.DictionaryListResponse;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Tag(
        name = "WordController",
        description = "Контроллер для работы со словарем",
        externalDocs = @ExternalDocumentation(
                description = "Документация в Confluence",
                url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                        "152436742/MS+-+dictionary"
        )
)
@SecurityRequirement(name = "JWT")
public interface WordApi {

    @Operation(
            summary = "Метод для поиска слов с учетом фильтров",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "152436778/POST+dictionary+list"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Слова получены успешно"),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации", content = @Content)
            }
    )
    DictionaryListResponse findWordsByFilter(@RequestBody @Validated DictionaryListRequest request);

    @Operation(
            summary = "Метод поиска слова по точному совпадению в словаре.",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "180781076/GET+dictionary+language+languageId+word+word"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Слово найдено успешно"),
                    @ApiResponse(responseCode = "404", description = "Слово не найдено", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации", content = @Content)
            }
    )
    DictionaryDetailedWordResponse getDetailedInfo(
            @Parameter(description = "искомое слово", example = "word")
            @PathVariable String word,
            @Parameter(description = "id языка", example = "2")
            @PathVariable Long languageId);

    @Operation(
            summary = "Метод добавления нового слова",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "180813845/CREATE+dictionary+word"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Слово успешно добавлено"),
                    @ApiResponse(responseCode = "400", description = "Слово уже существует"),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен"),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации")
            }
    )
    void createNewWord(@RequestBody @Validated CreateWordRequest request);

    @Operation(
            summary = "Метод редактирования слова",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "180781097/POST+dictionary+word"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Слово обновлено успешно"),
                    @ApiResponse(responseCode = "400", description = "Слово существует"),
                    @ApiResponse(responseCode = "404", description = "Слово не найдено"),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен"),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации")
            }
    )
    void updateWord(@RequestBody @Validated UpdateWordRequest request,
                    @Parameter(description = "id слова", example = "10")
                    @PathVariable Long id);

    @Operation(
            summary = "Метод удаления слова из словаря",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "180977731/DELETE+dictionary+word+id"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Слово удалено успешно"),
                    @ApiResponse(responseCode = "404", description = "Слово не найдено"),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен"),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации")
            }
    )
    void deleteWord(@Parameter(description = "id слова", example = "12")
                    @PathVariable Long id);

    @Operation(
            summary = "Получение словаря в формате JSON",
            description = "Метод позволяет выкачать весь словарь по id языка в JSON файл",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "180944985/GET+dictionary+word+list"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Успешная выгрузка слов"),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации", content = @Content)
            }
    )
    ResponseEntity<StreamingResponseBody> getAllWords(@Parameter(description = "id языка", example = "12")
                                                      @PathVariable Long languageId
    );
}
