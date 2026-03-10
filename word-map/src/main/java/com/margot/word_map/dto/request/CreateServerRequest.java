package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на создание сервера игрового мира.")
public class CreateServerRequest {

    @NotNull
    @Schema(description = "id платформы", example = "1")
    private Long platform;

    @NotNull
    @Schema(description = "id языка", example = "1")
    private Long language;

    @Schema(description = "Произвольное название сервера.", example = "Тест Яндекса")
    @Pattern(
            regexp = "^[a-zA-Zа-яА-ЯёЁ0-9\\-:() ]{1,100}$"
    )
    private String name;
}
