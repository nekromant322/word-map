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
@Schema(description = "Ответ с информацией о слове из словаря")
public class DictionaryWordResponse {

    @Schema(description = "Идентификатор слова в словаре", example = "123")
    private Long id;

    @Schema(description = "Слово из словаря", example = "лопата")
    private String word;

    @Schema(description = "Описание слова", example = "Инструмент для копания земли")
    private String description;
}
