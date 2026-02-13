package com.margot.word_map.dto.request;

import com.margot.word_map.validation.annotation.ValidEmail;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос админа на вход по почте")
public record AdminLoginRequest(

        @ValidEmail
        @Schema(description = "Почта пользователя", example = "mail123@gmail.com")
        String email
) {
}
