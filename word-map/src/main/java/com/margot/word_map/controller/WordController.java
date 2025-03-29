package com.margot.word_map.controller;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.request.DictionaryListRequest;
import com.margot.word_map.dto.request.UpdateWordRequest;
import com.margot.word_map.dto.response.DictionaryListResponse;
import com.margot.word_map.dto.response.DictionaryWordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dictionary")
public class WordController {

    @PostMapping("/list")
    public DictionaryListResponse findWordsByFilter(DictionaryListRequest request) {
        return DictionaryListResponse.builder().build();
    }

    @GetMapping("/{word}")
    public DictionaryWordResponse getWordInfo(@PathVariable String word) {
        return DictionaryWordResponse.builder().build();
    }

    @PostMapping("/word")
    public void createNewWord(@RequestBody CreateWordRequest request) {
        // создание нового слова
    }

    @PutMapping("/word")
    public void updateWord(@RequestBody UpdateWordRequest request) {
        // логирование в конфле
//        При редактировании записи, найти id = id и выполнить запись параметров:
//        word = word
//        description = description
//        lenght = количеству знаков в word
//        edited_at = текущей дате
//        id_edited = id пользователя из DB admins
    }

    @DeleteMapping("/word/{id}")
    public void deleteWord(@PathVariable Long id) {
//        При успешном выполнении операции необходимо выполнить логирование:
//        {time}: DELETE WORD Пользователь {email} удалил слово {word}.
    }

    @GetMapping("/word/list")
    public List<DictionaryWordResponse> getAllWords() {
        return List.of();
    }
}
