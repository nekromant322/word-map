package com.margot.word_map.controller;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.response.WordOfferResponse;
import com.margot.word_map.service.words_offer_service.WordsOfferService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
        description = "Контроллер для предложений слов пользователями и проверки админом",
        externalDocs = @ExternalDocumentation(
                description = "документация в Confluence",
                url = "документация для предложений слов еще не описана"
        )
)
@SecurityRequirement(name = "JWT")
@RestController
@RequiredArgsConstructor
@RequestMapping("/wordsOffer")
public class WordsOfferController {

    private final WordsOfferService wordsOfferService;

    @Operation(
            summary = "Метод для предложения слова",
            externalDocs = @ExternalDocumentation(
                    description = "Метод еще не описан в Confluence"
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Слово предложено успешно"),
                    @ApiResponse(responseCode = "400", description = "Слово уже предложено", content = @Content()),
                    @ApiResponse(responseCode = "400", description = "Слово уже существует", content = @Content()),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен", content = @Content()),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации", content = @Content())
            }
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
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Предложения получены успешно"),
                    @ApiResponse(responseCode = "400", description = "Ошибка в параметрах", content = @Content()),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен", content = @Content()),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации", content = @Content())
            }
    )
    @GetMapping("/admin/check")
    public Page<WordOfferResponse> checkOffers(
            @Parameter(description = "номер страницы", example = "2")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "количество элементов на странице", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return wordsOfferService.getAllWordsOffersNotChecked(pageable);
    }

    @Operation(
            summary = "Метод для принятия предложения",
            externalDocs = @ExternalDocumentation(
                    description = "Метод еще не описан в Confluence"
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Предложение принято успешно"),
                    @ApiResponse(responseCode = "400", description = "Ошибка в параметрах"),
                    @ApiResponse(responseCode = "400", description = "Слово уже существует"),
                    @ApiResponse(responseCode = "404", description = "Предложение не найдено"),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен"),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации")
            }
    )
    @PostMapping("/admin/approve/{id}")
    public void approveWord(@AuthenticationPrincipal UserDetails userDetails,
                            @Parameter(description = "id предложения", example = "12")
                            @PathVariable Long id) {
        wordsOfferService.approve(userDetails, id);
    }

    @Operation(
            summary = "Метод для отказа предложения",
            externalDocs = @ExternalDocumentation(
                    description = "Метод еще не описан в Confluence"
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Предложения отклонено успешно"),
                    @ApiResponse(responseCode = "400", description = "Ошибка в параметрах"),
                    @ApiResponse(responseCode = "404", description = "Предложение не найдено"),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен"),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации")
            }
    )
    @PostMapping("/admin/reject/{id}")
    public void rejectWord(@AuthenticationPrincipal UserDetails userDetails,
                           @Parameter(description = "id предложения", example = "12")
                           @PathVariable Long id) {
        wordsOfferService.reject(userDetails, id);
    }
}
