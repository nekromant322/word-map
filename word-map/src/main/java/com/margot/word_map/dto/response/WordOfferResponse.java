package com.margot.word_map.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Schema(description = "Описание предложенного слова", example = "Сочный плод с красной мякотью")
    private String description;
}
