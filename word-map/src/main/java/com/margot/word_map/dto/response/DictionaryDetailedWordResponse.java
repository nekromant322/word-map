package com.margot.word_map.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Schema(description = "Ответ с информацией о слове из словаря")
public class DictionaryDetailedWordResponse {

    @Schema(description = "Идентификатор слова в словаре", example = "123")
    private Long id;

    @Schema(description = "Слово из словаря", example = "лопата")
    private String word;

    @Schema(description = "Описание слова", example = "Инструмент для копания земли")
    private String description;

    @Schema(description = "Длина слова", example = "6")
    private Integer length;

    @Schema(description = "Дата создания", example = "25.11.2025 13:01")
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdAt;

    @Schema(description = "Email пользователя, который создал слово", example = "admin@mail.ru")
    private String creatorEmail;

    @Schema(description = "Дата изменения", example = "30.11.2025 15:43")
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime editedAt;

    @Schema(description = "Email пользователя, который изменил слово", example = "admin@mail.ru")
    private String editorEmail;
}
