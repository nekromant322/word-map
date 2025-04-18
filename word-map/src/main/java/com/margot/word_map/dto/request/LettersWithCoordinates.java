package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Буква и ее координаты")
public class LettersWithCoordinates {

    @NotBlank
    @Schema(description = "Одна буква", example = "A")
    private Character letters;

    @Schema(description = "Координаты", example = "1,5")
    private CoordinatesXY coordinates;
}


