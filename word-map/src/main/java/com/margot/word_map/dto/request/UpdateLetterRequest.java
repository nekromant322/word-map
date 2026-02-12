package com.margot.word_map.dto.request;

import com.margot.word_map.model.LetterType;
import com.margot.word_map.validation.annotation.EnumSubset;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на редактирование буквы алфавита")
public class UpdateLetterRequest {

    @EnumSubset(anyOf = { "Vowel", "Consonant" })
    @Schema(description = "Тип буквы", example = "а")
    private LetterType type;

    @Min(1) @Max(10)
    @Schema(description = "Множитель бонуса за букву", example = "1")
    private Short multiplier;

    @Min(1) @Max(50)
    @Schema(description = "Вес буквы (частота выпадения)", example = "6")
    private Short weight;
}
