package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Позиция клетки на карте с координатами X и Y")
public class Position  {

    @NotNull
    @Schema(description = "Координата X", example = "1")
    private Double x;

    @NotNull
    @Schema(description = "Координата Y", example = "5")
    private Double y;
}
