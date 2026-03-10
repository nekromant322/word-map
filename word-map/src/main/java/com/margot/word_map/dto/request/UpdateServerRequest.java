package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на изменение названия сервера игрового мира.")
public class UpdateServerRequest {

    @Schema(description = "Произвольное название сервера.", example = "Тест Яндекса")
    @Pattern(
            regexp = "^[a-zA-Zа-яА-ЯёЁ0-9\\-:() ]{0,100}$"
    )
    private String name;
}
