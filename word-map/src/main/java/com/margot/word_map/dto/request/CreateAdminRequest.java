package com.margot.word_map.dto.request;

import com.margot.word_map.dto.AdminType;
import com.margot.word_map.model.Rule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @Email(message = "Неверный формат email")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Некорректный формат email")
    @NotBlank(message = "Email не может быть пустым")
    @Schema(description = "Email пользователя", example = "example@mail.com")
    private String email;

    @NotNull(message = "Поле access не может быть пустым")
    @Schema(description = "Доступ", example = "true/false")
    private Boolean access;

    @NotNull(message = "Поле adminType не может быть пустым")
    @Schema(description = "Тип администратора", example = "ADMIN,MODERATOR")
    private AdminType adminType;

    @NotNull(message = "Роли не могут быть пустыми")
    @Schema(description = "Массив ролей", example = "MANAGE_DICTIONARY, " +
            "WIPE_DICTIONARY, MANAGE_RATING, MANAGE_WORLD," +
            " MANAGE_ROLE, MANAGE_IVENT, MANAGE_SHOP")
    private List<Rule.RULE> roles;
}