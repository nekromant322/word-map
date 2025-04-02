package com.margot.word_map.controller;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.request.DictionaryListRequest;
import com.margot.word_map.dto.request.UpdateWordRequest;
import com.margot.word_map.dto.response.DictionaryListResponse;
import com.margot.word_map.dto.response.DictionaryWordResponse;
import com.margot.word_map.service.WordService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/dictionary")
public class WordController {

    private final WordService wordService;

    @PostMapping("/list")
    public DictionaryListResponse findWordsByFilter(DictionaryListRequest request) {
        return DictionaryListResponse.builder().build();
    }

    @GetMapping("/{word}")
    public DictionaryWordResponse getWordInfo(@PathVariable String word) {
        return wordService.getWordInfo(word);
    }

    @PostMapping("/word")
    public void createNewWord(@AuthenticationPrincipal UserDetails userDetails,
                              @RequestBody @Validated CreateWordRequest request) {
        wordService.createNewWord(userDetails, request);
    }

    @PutMapping("/word")
    public void updateWord(@AuthenticationPrincipal UserDetails userDetails,
                           @RequestBody @Validated UpdateWordRequest request) {
        wordService.updateWordInfo(userDetails, request);
    }

    @DeleteMapping("/word/{id}")
    public void deleteWord(@AuthenticationPrincipal UserDetails userDetails,
                           @PathVariable Long id) {
        wordService.deleteWord(userDetails, id);
    }

    @GetMapping("/word/list")
    public ResponseEntity<StreamingResponseBody> getAllWords() throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=words.json") // Указываем имя файла
                .body(wordService.getAllWords());
    }
}
