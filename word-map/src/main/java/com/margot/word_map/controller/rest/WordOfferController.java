package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.WordOfferApi;
import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.request.WordOfferChangeStatus;
import com.margot.word_map.dto.request.WordOffersSortRequest;
import com.margot.word_map.dto.response.WordOfferResponse;
import com.margot.word_map.service.word_offer.WordOfferService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wordsOffer")
public class WordOfferController implements WordOfferApi {

    private final WordOfferService wordOfferService;

    @PostMapping("/offer")
    @Override
    public void offerWord(@RequestBody CreateWordRequest word) {
        wordOfferService.processWordOffer(word);
    }

    @PostMapping("/admin/list")
    @Override
    public Page<WordOfferResponse> getWordOffers(
            @RequestBody WordOffersSortRequest request) {
        return wordOfferService.getOffers(
                request.getStatus(), request.getPage(), request.getSize(), request.getSortBy(), request.getSortDir());
    }

    @PostMapping("/status")
    @Override
    public void changeStatus(@RequestBody WordOfferChangeStatus status) {
        wordOfferService.changeStatus(status);
    }

    @PostMapping("/admin/reject/{id}")
    @Override
    public void rejectWord(@Parameter(description = "id предложения", example = "12")
                           @PathVariable Long id) {
        wordOfferService.reject(id);
    }

    @PostMapping("/admin/approve/{id}")
    @Override
    public void approveWord(@Parameter(description = "id предложения", example = "12")
                            @PathVariable Long id,
                            @Parameter(description = "Описание слова", example = "Инструмент для ковки")
                            @RequestParam String description) {
        wordOfferService.approve(id, description);
    }
}
