package com.margot.word_map.controller;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.response.WordOfferResponse;
import com.margot.word_map.service.words_offer_service.WordsOfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wordsOffer")
public class WordsOfferController {

    private final WordsOfferService wordsOfferService;

    //Потом поменяем на User с Admin
    @PostMapping("/offer")
    public ResponseEntity<String> offerWord(@RequestBody CreateWordRequest word,
                                            @AuthenticationPrincipal UserDetails userDetails) {

        wordsOfferService.processWordOffer(word, userDetails);
        return ResponseEntity.ok("Слово принято на рассмотрение");
    }

    @GetMapping("/admin/check")
    public ResponseEntity<Page<WordOfferResponse>> checkOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(wordsOfferService.getAllWordsOffersNotChecked(pageable));
    }

    @PostMapping("/admin/approve/{id}")
    public void approveWord(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Long id) {
        wordsOfferService.approve(userDetails, id);
    }

    @PostMapping("/admin/reject/{id}")
    public void rejectWord(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable Long id) {
        wordsOfferService.reject(userDetails, id);
    }
}
