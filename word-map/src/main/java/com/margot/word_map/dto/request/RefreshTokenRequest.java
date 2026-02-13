package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос на обновление access token ")
public record RefreshTokenRequest(

        @Schema(description = "refresh token клиента")
        @NotBlank(message = "Токен не может быть пустым")
        String refreshToken) {
}
