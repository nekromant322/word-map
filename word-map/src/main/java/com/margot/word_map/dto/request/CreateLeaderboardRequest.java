package com.margot.word_map.dto.request;

import com.margot.word_map.model.enums.Period;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос для получения полного списка лидербордов")
public class CreateLeaderboardRequest {
    @NotNull
    @Schema(description = "Название платформы", example = "1")
    private String platformId;

    @NotNull
    @Schema(description = "Id языка", example = "1")
    private Long languageId;

    @NotNull
    @Schema(description = "Тип периода таблицы лидеров.", example = "DAY")
    private Period period;
}
