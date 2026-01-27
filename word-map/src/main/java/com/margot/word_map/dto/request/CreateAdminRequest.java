package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос создания администратора")
public class CreateAdminRequest {

    @Email(message = "EMAIL_FORMAT_ERROR", regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    @NotBlank(message = "EMAIL_FORMAT_ERROR")
    @Schema(description = "Email пользователя", example = "example@mail.com")
    private String email;

    @Schema(description = "Массив идентификаторов прав", example = "1, 2, 3")
    private List<Long> ruleID;
}