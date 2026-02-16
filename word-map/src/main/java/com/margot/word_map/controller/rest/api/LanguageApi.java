package com.margot.word_map.controller.rest.api;

import com.margot.word_map.dto.LanguageDto;
import com.margot.word_map.dto.OptionDto;
import com.margot.word_map.dto.request.CreateUpdateLanguageRequest;
import com.margot.word_map.dto.response.LetterResponse;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "LanguageController",
        description = "Контроллер для работы с языками"
)
@SecurityRequirement(name = "JWT")
public interface LanguageApi {

    @Operation(
            summary = "Создание языка",
            description = "Запрос для создания языка",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/395640833/POST+language"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Язык создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректный формат ввода"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
                    @ApiResponse(responseCode = "409", description = "Префикс уже существует"),
                    @ApiResponse(responseCode = "409", description = "Название уже существует")
            }
    )
    LanguageDto createLanguage(@RequestBody @Valid CreateUpdateLanguageRequest request);

    @Operation(
            summary = "Редактирование языка",
            description = "Запрос для обновления языка",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/395640841/PUT+language+id"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Язык обновлен"),
                    @ApiResponse(responseCode = "400", description = "Некорректный формат ввода"),
                    @ApiResponse(responseCode = "401", description = "Токен доступа недействителен"),
                    @ApiResponse(responseCode = "403", description = "Аккаунт заблокирован"),
                    @ApiResponse(responseCode = "403", description = "Недостаточно прав"),
                    @ApiResponse(responseCode = "404", description = "Аккаунт не найден"),
                    @ApiResponse(responseCode = "404", description = "Язык не найден"),
                    @ApiResponse(responseCode = "409", description = "Префикс уже существует"),
                    @ApiResponse(responseCode = "409", description = "Название уже существует")
            }
    )
    LanguageDto updateLanguage(
            @PathVariable Long id,
            @RequestBody @Valid CreateUpdateLanguageRequest request);

    @Operation(
            summary = "Список языков",
            description = "Метод получения списка доступных языков",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/404226049/GET+language+list"
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
    List<LanguageDto> getLanguages();

    @Operation(
            summary = "Список языков",
            description = "Метод получения списка доступных языков",
            externalDocs = @ExternalDocumentation(
                    description = "документация запроса в Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/385417392/GET+language+option"
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
    List<OptionDto> getLanguageOptions();

    @GetMapping("/{id}/alphabet")
    List<LetterResponse> getAlphabet(@PathVariable("id") Long id);
}

