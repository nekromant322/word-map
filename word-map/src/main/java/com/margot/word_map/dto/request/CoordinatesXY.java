package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Координаты X и Y")
public class CoordinatesXY {

    @Schema(description = "Координата X", example = "1")
    private double x;

    @Schema(description = "Координата Y", example = "5")
    private double y;
}
