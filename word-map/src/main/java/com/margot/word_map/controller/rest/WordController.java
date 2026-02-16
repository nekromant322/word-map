package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.WordApi;
import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.request.DictionaryListRequest;
import com.margot.word_map.dto.request.UpdateWordRequest;
import com.margot.word_map.dto.response.DictionaryDetailedWordResponse;
import com.margot.word_map.dto.response.DictionaryListResponse;
import com.margot.word_map.service.word.WordService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dictionary")
public class WordController implements WordApi {

    private final WordService wordService;

    @PostMapping("/search")
    public DictionaryListResponse findWordsByFilter(@RequestBody @Validated DictionaryListRequest request) {
        return wordService.getWordsByFilters(request);
    }

    @GetMapping("/language/{languageId}/word/{word}")
    public DictionaryDetailedWordResponse getDetailedInfo(
            @Parameter(description = "искомое слово", example = "word")
            @PathVariable String word,
            @Parameter(description = "id языка", example = "2")
            @PathVariable Long languageId) {
        return wordService.getWordByLanguageId(languageId, word);
    }

    @PostMapping("/word")
    public void createNewWord(@RequestBody @Validated CreateWordRequest request) {
        wordService.createNewWord(request);
    }

    @PutMapping("/word/{id}")
    public void updateWord(@RequestBody @Validated UpdateWordRequest request,
                           @Parameter(description = "id слова", example = "10")
                           @PathVariable Long id) {
        wordService.updateWordInfo(request, id);
    }

    @DeleteMapping("/word/{id}")
    public void deleteWord(@Parameter(description = "id слова", example = "12")
                           @PathVariable Long id) {
        wordService.deleteWord(id);
    }

    @GetMapping("/download/{languageId}")
    public ResponseEntity<StreamingResponseBody> getAllWords(@Parameter(description = "id языка", example = "12")
                                                             @PathVariable Long languageId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=words.json")
                .body(wordService.getAllWordsByLanguageId(languageId));
    }
}
