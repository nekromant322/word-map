package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Буква и ее координаты")
public class LettersWithCoordinates {

    @NotNull
    @Schema(description = "Одна буква", example = "A")
    private Character letter;

    @Valid
    @NotNull
    @Schema(description = "Позиция с координатами", example = "1,5")
    private Position position;
}


