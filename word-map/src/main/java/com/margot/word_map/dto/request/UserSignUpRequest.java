package com.margot.word_map.dto.request;

import jakarta.validation.constraints.Email;
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
public class UserSignUpRequest {

    @Email(message = "Неверный формат email")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Некорректный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    @Pattern(
            regexp = "^(?![._])(?!.*[._]{2})[a-zA-Z0-9._]{3,20}(?<![._])$",
            message = "Имя пользователя должно содержать от 3 до 20 символов, состоять из латинских букв, цифр, '.', '_', не начинаться и не заканчиваться на '.' или '_', и не содержать подряд '..' или '__'"
    )
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;
}

// правила для username
// Только латинские буквы, цифры, подчёркивания и точки
// Без пробелов
// Длина от 3 до 20 символов
// Не начинается и не заканчивается с . или _
// Не содержит подряд .. или __