package com.margot.word_map.dto.request;

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
@Schema(description = "Позиция символа в слове")
public class SymbolPosition {

    @Schema(description = "Позиция символа в слове (начиная с 0)", example = "2")
    @NotNull
    @Min(0)
    @Max(99)
    private Integer number;

    @Schema(description = "Символ", example = "а")
    @NotNull
    private Character letter;
}
