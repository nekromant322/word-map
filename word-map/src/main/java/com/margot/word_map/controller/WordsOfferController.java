package com.margot.word_map.controller;

import com.margot.word_map.model.Admin;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/words")
public class WordsOfferController {

    @PostMapping("/offer")
    public ResponseEntity<String> offerWord(@RequestParam String word, @AuthenticationPrincipal Admin admin) {
        // создаем репозиторий для новых слов, а
        // потом метод который возвращает слова, если все хорошо, то сохраняем в общую таблицу
        // и удаляем из временной таблицы офферов
        return ResponseEntity.ok("Слово принято на рассмотрение");
    }

//    @GetMapping("/check")
//    public ResponseEntity<List<WordOffers>> checkOffers() {
//
//    }

//    @PostMapping("/admin/approve/{id}")
//    public ResponseEntity<String> approveWord(@PathVariable Long id) {
//        wordOfferService.approveWord(id);
//        return ResponseEntity.ok("Слово добавлено в базу");
//    }
//
//    @PostMapping("/admin/reject/{id}")
//    public ResponseEntity<String> rejectWord(@PathVariable Long id) {
//        wordOfferService.rejectWord(id);
//        return ResponseEntity.ok("Слово отклонено");
//    }
}
