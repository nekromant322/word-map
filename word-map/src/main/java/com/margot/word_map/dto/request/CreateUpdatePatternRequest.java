package com.margot.word_map.dto.request;

import com.margot.word_map.model.PatternCell;
import com.margot.word_map.model.enums.PatternType;
import com.margot.word_map.validation.annotation.StrictDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@StrictDto
@Schema(description = "Запрос на создание/обновление паттерна")
public class CreateUpdatePatternRequest {

    @Schema(description = "Признак черновика",
            examples = {
                "true - черновик (не используется при генерации)",
                "false - опубликован (используется при генерации)"
            }
    )
    private Boolean isDraft;

    @Schema(description = "Тип паттерна для определения стыкуемости")
    private PatternType patternType;

    @Min(value = 1, message = "Минимальное значение веса 1")
    @Max(value = 999, message = "Максимальное значение веса 999")
    @Schema(description = "Вес паттерна для рандомизации")
    private Short weight;

    @Size(min = 49, max = 49, message = "Массив должен содержать 49 элементов")
    @Schema(description = "JSON с параметрами 49 тайлов")
    private List<PatternCell> cells;
}
