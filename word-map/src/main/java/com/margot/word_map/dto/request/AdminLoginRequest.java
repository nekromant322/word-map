package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Запрос админа на вход по почте")
public record AdminLoginRequest(

        @Schema(description = "Почта пользователя", example = "mail123@gmail.com")
        @Email(
                regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
                message = "EMAIL_FORMAT_ERROR")
        @NotBlank(message = "EMAIL_FORMAT_ERROR")
        String email
) {
}
