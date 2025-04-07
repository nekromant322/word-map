package com.margot.word_map.controller;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.request.DictionaryListRequest;
import com.margot.word_map.dto.request.UpdateWordRequest;
import com.margot.word_map.dto.response.DictionaryListResponse;
import com.margot.word_map.dto.response.DictionaryWordResponse;
import com.margot.word_map.service.WordService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @PostMapping("/list")
    public DictionaryListResponse findWordsByFilter(@RequestBody @Validated DictionaryListRequest request) {
        return DictionaryListResponse.builder().build();
    }

    @Operation(
            summary = "Метод поиска слова по точному совпадению в словаре.",
            externalDocs = @ExternalDocumentation(
                    description = "Confluence",
                    url = "https://override-platform.atlassian.net/wiki/spaces/W/pages/" +
                            "180781076/GET+dictionary+word+word"
            )
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
    @GetMapping("/word/list")
    public ResponseEntity<StreamingResponseBody> getAllWords() throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=words.json")
                .body(wordService.getAllWords());
    }
}
