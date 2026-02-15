package com.margot.word_map.dto.response;

import com.margot.word_map.model.LetterType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ответ содержащий данные о букве")
public class LetterResponse {

    @Schema(description = "Уникальный идентификатор языка", example = "1")
    private Long id;

    @Schema(description = "Буква", example = "а")
    private Character letter;

    @Schema(description = "Тип буквы", example = "Vowel | Consonant")
    private LetterType type;

    @Schema(description = "Множитель бонуса за букву", example = "1")
    private Short multiplier;

    @Schema(description = "Вес буквы (частота выпадения)", example = "6")
    private Short weight;
}
