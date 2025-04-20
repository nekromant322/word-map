package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Запрос для подтверждения проверочного кода")
public class ConfirmRequest {

    @Schema(description = "Email пользователя", example = "example@mail.com")
    @NotBlank(message = "Invalid. Email can not be blank")
    @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format. Example: example@mail.com")
    private String email;

    @Schema(description = "Код проверки, введенный пользователем", example = "894091")
    @NotBlank(message = "Invalid. Code can not be blank")
    @Size(min = 6, max = 6, message = "Invalid. Code size must be 6")
    private String code;
}