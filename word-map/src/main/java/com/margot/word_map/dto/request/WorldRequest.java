package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запрос на создание мира")
public class WorldRequest {

    @Schema(description = "id языка", example = "2")
    private Long language;

    @Schema(description = "id платформы", example = "1")
    private Long platform;
}
