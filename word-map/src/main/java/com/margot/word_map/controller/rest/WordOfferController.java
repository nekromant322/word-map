package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.request.CreateWordRequest;
import com.margot.word_map.dto.request.WordOfferChangeStatus;
import com.margot.word_map.dto.request.WordOffersSortRequest;
import com.margot.word_map.dto.response.WordOfferResponse;
import com.margot.word_map.service.word_offer.WordOfferService;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
public class WordOfferController {

    private final WordOfferService wordOfferService;

    @Operation(
            summary = "Метод для предложения слова",
            externalDocs = @ExternalDocumentation(
                    description = "https://override-platform.atlassian.net/wiki/spaces/W/pages/209715201/POST+offer"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Слово предложено успешно"),
                    @ApiResponse(responseCode = "400", description = "Слово уже предложено"),
                    @ApiResponse(responseCode = "400", description = "Слово уже существует"),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен"),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации")
            }
    )
    @PostMapping("/offer")
    public void offerWord(@RequestBody CreateWordRequest word,
                          @AuthenticationPrincipal UserDetails userDetails) {
        wordOfferService.processWordOffer(word, userDetails);
    }

    @Operation(
            summary = "Метод для просмотра предложений",
            externalDocs = @ExternalDocumentation(
                    description =
                            "https://override-platform.atlassian.net/wiki/spaces/W/pages/207945769/POST+offer+list"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Предложения получены успешно"),
                    @ApiResponse(responseCode = "400", description = "Ошибка в параметрах", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Устаревший токен", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Ошибка авторизации", content = @Content)
            }
    )
    @PostMapping("/admin/list")
    public Page<WordOfferResponse> getWordOffers(
            @RequestBody WordOffersSortRequest request) {
        return wordOfferService.getOffers(
                request.getStatus(), request.getPage(), request.getSize(), request.getSortBy(), request.getSortDir());
    }

    @Operation(
            summary = "Метод для изменения статусов слов",
            externalDocs = @ExternalDocumentation(
                    description =
                            "https://override-platform.atlassian.net/wiki/spaces/W/pages/209813505/POST+offer+status"
            )
    )
    @PostMapping("/status")
    public void changeStatus(@RequestBody WordOfferChangeStatus status) {
        wordOfferService.changeStatus(status);
    }

    @Operation(
            summary = "Метод для отказа предложения",
            externalDocs = @ExternalDocumentation(
                    description = "Метод еще не описан в Confluence"
            )
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Отклонено успешно"),
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
        wordOfferService.reject(userDetails, id);
    }

    @Operation(
            summary = "Метод для принятия предложения",
            externalDocs = @ExternalDocumentation(
                    description = "Метод еще не описан в Confluence"
            )
    )
    @ApiResponses(
            value = {
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
                            @PathVariable Long id,
                            @Parameter(description = "Описание слова", example = "Инструмент для ковки")
                            @RequestParam String description) {
        wordOfferService.approve(userDetails, id, description);
    }
}
