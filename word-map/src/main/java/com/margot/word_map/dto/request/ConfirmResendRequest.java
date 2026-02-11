package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос для повторной отправки проверочного кода")
public record ConfirmResendRequest(

        @Schema(description = "Идентификатор кода подтверждения", example = "12")
        @NotNull(message = "Идентификатор кода подтверждения обязателен")
        Long confirmID
) {
}
