package com.margot.word_map.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Ответ, содержащий access и refresh токены")
public class TokenResponse {

    @Schema(description = "JWT access токен, используемый для авторизации", example = "eyJhbGciOiJIUzI1...")
    private String accessToken;

    @Schema(description = "JWT refresh токен, используемый для обновления access токена", example = "dGhpcy1pc...")
    private String refreshToken;
}
