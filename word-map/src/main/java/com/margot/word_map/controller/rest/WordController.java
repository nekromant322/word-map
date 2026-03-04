package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.WordApi;
import com.margot.word_map.dto.request.*;
import com.margot.word_map.dto.response.*;
import com.margot.word_map.service.word.WordService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<DictionaryWordResponse>> getAllWords(@Parameter(description = "id языка", example = "12")
                                                                    @PathVariable Long languageId) {
        List<DictionaryWordResponse> words = wordService.getAllWordsByLanguageId(languageId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"words.json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(words);
    }

    @PostMapping("/offer")
    public OfferResponse offerWord(@RequestBody @Validated CreateWordOfferRequest word) {
        return wordService.processWordOffer(word);
    }

    @PutMapping("offer/status")
    public void changeStatus(@RequestBody @Validated WordOfferChangeStatus status) {
        wordService.changeStatus(status);
    }

    @GetMapping("/offer/list")
    public List<OfferListResponse> getAllPlayerOffers() {
        return wordService.getAllPlayerOffers();
    }

    @PostMapping("/offer/list")
    public WordOfferAdminResponse getAllAdminOffers(@RequestBody @Validated WordOfferAdminRequest request,
                                                    Pageable pageable) {
        return wordService.getAllAdminOffers(request, pageable);
    }
}
