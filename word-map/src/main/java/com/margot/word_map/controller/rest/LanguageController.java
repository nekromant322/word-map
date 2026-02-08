package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.LanguageDto;
import com.margot.word_map.dto.request.CreateUpdateLanguageRequest;
import com.margot.word_map.service.language.LanguageService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "LanguageController",
        description = "Контроллер для работы с языками"
)
@SecurityRequirement(name = "JWT")
@RestController
@RequiredArgsConstructor
@RequestMapping("/language")
public class LanguageController {

    private final LanguageService languageService;

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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LanguageDto createLanguage(@RequestBody @Valid CreateUpdateLanguageRequest request) {
        return languageService.createLanguage(request);
    }
}
