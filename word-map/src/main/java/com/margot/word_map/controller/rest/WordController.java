package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.request.DictionaryListRequest;
import com.margot.word_map.dto.request.UpdateWordRequest;
import com.margot.word_map.dto.response.DictionaryListResponse;
import com.margot.word_map.dto.response.DictionaryWordResponse;
import com.margot.word_map.service.word.WordService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;

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
@RestController
@RequiredArgsConstructor
@RequestMapping("/dictionary")
public class WordController {

    private final WordService wordService;

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
    @PostMapping("/list")
    public DictionaryListResponse findWordsByFilter(@RequestBody @Validated DictionaryListRequest request) {
        return wordService.getWordsByFilters(request);
    }

    @Operation(
            summary = "Метод поиска слова по точному совпадению в словаре.",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "180781076/GET+dictionary+word+word"
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
    @GetMapping("/word/{word}")
    public DictionaryWordResponse getWordInfo(
            @Parameter(description = "искомое слово", example = "word")
            @PathVariable String word) {
        return wordService.getWordInfo(word);
    }

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
    @PostMapping("/word")
    public void createNewWord(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestBody @Validated CreateWordRequest request) {
        wordService.createNewWord(userDetails, request);
    }

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
    @PutMapping("/word")
    public void updateWord(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestBody @Validated UpdateWordRequest request) {
        wordService.updateWordInfo(userDetails, request);
    }

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
    @DeleteMapping("/word/{id}")
    public void deleteWord(@AuthenticationPrincipal UserDetails userDetails,
                           @Parameter(description = "id слова", example = "12")
                           @PathVariable Long id) {
        wordService.deleteWord(userDetails, id);
    }

    @Operation(
            summary = "Получение словаря в формате JSON",
            description = "Метод позволяет выкачать весь словарь в JSON файл",
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
    @GetMapping("/word/list")
    public ResponseEntity<StreamingResponseBody> getAllWords() throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=words.json")
                .body(wordService.getAllWords());
    }
}
