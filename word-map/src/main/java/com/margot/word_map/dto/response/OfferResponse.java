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
@Schema(description = "Ответ, содержащий предложенное слово")
public class OfferResponse {
    @Schema(description = "Идентификатор слова из предложки", example = "123")
    private Long id;

    @Schema(description = "Слово из словаря", example = "лопата")
    private String word;
}
