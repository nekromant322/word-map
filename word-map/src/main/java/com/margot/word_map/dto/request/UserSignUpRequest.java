package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на регистрацию игрока")
public class UserSignUpRequest {

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Некорректный формат email")
    @NotBlank(message = "Email не может быть пустым")
    @Schema(description = "Почта игрока", example = "test@mail.ru")
    private String email;

    @Pattern(
            regexp = "^(?![._])(?!.*[._]{2})[a-zA-Z0-9._]{3,20}(?<![._])$",
            message = "Имя пользователя должно содержать от 3 до 20 символов, " +
                    "состоять из латинских букв, цифр, '.', '_', " +
                    "не начинаться и не заканчиваться на '.' или '_', " +
                    "не содержать подряд '..' или '__'"
    )
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Schema(description = "Имя пользователя в игре", example = "bahertylop")
    private String username;
}