package com.margot.word_map.controller;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.response.WordOfferResponse;
import com.margot.word_map.service.words_offer_service.WordsOfferService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "WordsOfferController",
        description = "Контроллер для предложений слов пользователями и проверки админом"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/wordsOffer")
public class WordsOfferController {

    private final WordsOfferService wordsOfferService;

    @Operation(
            summary = "Метод для предложения слова",
            externalDocs = @ExternalDocumentation(
                    description = "Метод еще не описан в Confluence"
            )
    )
    @PostMapping("/offer")
    public void offerWord(@RequestBody CreateWordRequest word,
                                            @AuthenticationPrincipal UserDetails userDetails) {

        wordsOfferService.processWordOffer(word, userDetails);
    }

    @Operation(
            summary = "Метод для просмотра предложений",
            externalDocs = @ExternalDocumentation(
                    description = "Метод еще не описан в Confluence"
            )
    )
    @GetMapping("/admin/check")
    public Page<WordOfferResponse> checkOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return wordsOfferService.getAllWordsOffersNotChecked(pageable);
    }

    @Operation(
            summary = "Метод для принятия предложения",
            externalDocs = @ExternalDocumentation(
                    description = "Метод еще не описан в Confluence"
            )
    )
    @PostMapping("/admin/approve/{id}")
    public void approveWord(@AuthenticationPrincipal UserDetails userDetails,
                                              @PathVariable Long id) {
        wordsOfferService.approve(userDetails, id);
    }

    @Operation(
            summary = "Метод для отказа предложения",
            externalDocs = @ExternalDocumentation(
                    description = "Метод еще не описан в Confluence"
            )
    )
    @PostMapping("/admin/reject/{id}")
    public void rejectWord(@AuthenticationPrincipal UserDetails userDetails,
                                             @PathVariable Long id) {
        wordsOfferService.reject(userDetails, id);
    }
}
