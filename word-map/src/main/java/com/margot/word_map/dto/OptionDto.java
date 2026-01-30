package com.margot.word_map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionDto {

    @Schema(description = "Название в зависимости от запрашиваемого ресурса (права, языки)",
            example = "MANAGE_DICTIONARY|ru")
    private String label;

    @Schema(description = "Уникальный идентификатор", example = "1")
    private String value;

    @Schema(description = "Описание", example = "")
    private String description;
}
