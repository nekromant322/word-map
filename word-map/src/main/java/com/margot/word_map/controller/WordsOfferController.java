package com.margot.word_map.controller;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.WordOffer;
import com.margot.word_map.service.words_offer_service.WordsOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/words")
public class WordsOfferController {

    private final WordsOfferService wordsOfferService;

    @PostMapping("/offer")
    public ResponseEntity<String> offerWord(@RequestBody CreateWordRequest word, @AuthenticationPrincipal Admin admin) {
        if (wordsOfferService.findByWordInTableWords(word.getWord())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Такое слово уже существует");
        } else if (wordsOfferService.findByWordInTableWordsOffer(word.getWord())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Такое слово уже было предложено");
        }
        wordsOfferService.save(new WordOffer(word.getWord(), word.getDescription()));
        return ResponseEntity.ok("Слово принято на рассмотрение");
    }

    @GetMapping("/check")
    public ResponseEntity<StreamingResponseBody> checkOffers() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=wordsOffer.json")
                .body(wordsOfferService.getAllWordsOffersNotChecked());
    }

    @PostMapping("/admin/approve/{id}")
    public ResponseEntity<String> approveWord(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Long id) {
        wordsOfferService.approve(userDetails, id);
        return ResponseEntity.ok("Слово добавлено в базу");
    }

    @PostMapping("/admin/reject/{id}")
    public ResponseEntity<String> rejectWord(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable Long id) {
        wordsOfferService.reject(userDetails, id);
        return ResponseEntity.ok("Слово отклонено");
    }
}
