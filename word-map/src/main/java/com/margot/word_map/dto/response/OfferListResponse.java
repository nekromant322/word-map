package com.margot.word_map.dto.response;

import com.margot.word_map.model.WordOfferStatus;
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
@Schema(description = "Ответ, содержащий предложенное слово")
public class OfferListResponse {
    @Schema(description = "Идентификатор слова из предложки", example = "123")
    private Long id;

    @Schema(description = "Слово из словаря", example = "лопата")
    private String word;

    @Schema(description = "Время создания записи", example = "2026-03-01 00:18:39.697095")
    private LocalDateTime createdAt;

    @Schema(description = "Статус", example = "approved")
    private WordOfferStatus status;
}
