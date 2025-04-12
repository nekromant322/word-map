package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Параметры фильтрации языков в словаре")
public class DictionaryListRequest {

    @Schema(description = "Язык выбранный пользователем в личном кабинете", example = "ru")
    private String language;

    @Schema(description = "Допускается ли повторение введенных в lettersUsed букв в одном слове", example = "true")
    private Boolean reuse;

    @Schema(description = "Буквы, которые должны присутствовать в словах", example = "апродушт")
    private String lettersUsed;

    @Schema(description = "Буквы, которые не могут присутствовать в словах", example = "лйч")
    private String lettersExclude;

    @Schema(description = "Длина слов, которые вернуться в ответе", example = "5")
    private Integer wordLength;

    @Schema(description = "Массив букв с их позицией в слове")
    private List<SymbolPosition> positions;
}
