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
@Schema(description = "Запрос на обновление слова в словаре")
public class UpdateWordRequest {

    @NotBlank(message = "описание не может быть пустым")
    @Schema(description = "Определение слова", example = "Инструмент для копания земли")
    private String description;
}
