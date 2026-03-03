package com.margot.word_map.dto;

import com.margot.word_map.model.WordOfferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Список предложенных слов для административной панели.")
public class WordOfferPage {
    @NotNull
    @Schema(description = "Предложенное слово", example = "апорт")
    private String word;

    @NotNull
    @Schema(description = "Дата и время создания самой поздней записи" +
            " в формате DD.MM.YYYY 24HH:MM", example = "01.02.2025 13:51")
    private LocalDateTime createdAt;

    @NotNull
    @Schema(description = "Статус рассмотрения предложенного слова.", example = "approved")
    private WordOfferStatus status;

    @NotNull
    @Schema(description = "Рейтинг слова (сумма предложений одного слова)", example = "28")
    private Long rating;
}
