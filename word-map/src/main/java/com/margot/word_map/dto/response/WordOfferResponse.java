package com.margot.word_map.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ответ с предложенным словом от пользователя")
public class WordOfferResponse {

    @Schema(description = "ID предложения", example = "1")
    private Long id;

    @Schema(description = "Предложенное слово", example = "арбуз")
    private String word;

    @Schema(description = "Предложенно слово в ", example = "2020-08-01 12-33")
    private LocalDateTime createdAt;

    @Schema(description = "Статус", example = "APPROVED")
    private String status;

    @Schema(description = "Id пользователя, кто предложил слово", example = "1")
    private Long userId;
}
