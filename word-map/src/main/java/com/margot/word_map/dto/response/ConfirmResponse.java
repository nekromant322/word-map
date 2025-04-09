package com.margot.word_map.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Ответ на запрос кода подтверждения")
public class ConfirmResponse {

    @Schema(description = "Ключ для поиска записи в auth.confirm", example = "12")
    private Long confirmID;

    @Schema(description = "Срок жизни проверочного кода в секундах", example = "300")
    private Integer lifetime;
}
