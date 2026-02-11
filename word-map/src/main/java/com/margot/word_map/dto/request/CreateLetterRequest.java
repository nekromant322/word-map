package com.margot.word_map.dto.request;

import com.margot.word_map.model.LetterType;
import com.margot.word_map.validation.annotation.EnumSubset;
import com.margot.word_map.validation.annotation.OnlyLetter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на создание буквы алфавита")
public class CreateLetterRequest {

    @NotNull
    @Schema(description = "Уникальный идентификатор языка", example = "1")
    private Long languageId;

    @NotNull
    @OnlyLetter
    @Schema(description = "Буква", example = "а")
    private Character letter;

    @NotNull
    @EnumSubset(anyOf = { "Vowel", "Consonant" })
    @Schema(description = "Тип буквы", example = "Vowel | Consonant")
    private LetterType type;

    @NotNull
    @Min(1) @Max(10)
    @Schema(description = "Множитель бонуса за букву", example = "1")
    private Short multiplier;

    @NotNull
    @Min(1) @Max(50)
    @Schema(description = "Вес буквы (частота выпадения)", example = "6")
    private Short weight;
}
