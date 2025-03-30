package com.margot.word_map.controller;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.request.DictionaryListRequest;
import com.margot.word_map.dto.request.UpdateWordRequest;
import com.margot.word_map.dto.response.DictionaryListResponse;
import com.margot.word_map.dto.response.DictionaryWordResponse;
import com.margot.word_map.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public void createNewWord(@RequestBody @Validated CreateWordRequest request) {
        wordService.createNewWord(request);
    }

    @PutMapping("/word")
    public void updateWord(@RequestBody UpdateWordRequest request) {
        wordService.updateWordInfo(request);
    }

    @DeleteMapping("/word/{id}")
    public void deleteWord(@PathVariable Long id) {
        wordService.deleteWord(id);
    }

    @GetMapping("/word/list")
    public ResponseEntity<StreamingResponseBody> getAllWords() throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(wordService.getAllWords());
    }
}
