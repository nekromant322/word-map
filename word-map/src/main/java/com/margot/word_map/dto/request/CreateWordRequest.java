package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на создание нового слова в словаре")
public class CreateWordRequest {

    @NotBlank(message = "Слово не может быть пустым")
    @Schema(description = "Слово, добавляемое в словарь", example = "лопата")
    private String word;

    @Schema(description = "Описание слова", example = "Инструмент для копания земли")
    private String description;

    @NotBlank(message = "Id языка не может быть пустым")
    @Schema(description = "Id языка", example = "1")
    private Long languageId;
}
