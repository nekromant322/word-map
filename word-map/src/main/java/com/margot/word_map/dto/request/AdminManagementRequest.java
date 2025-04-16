package com.margot.word_map.dto.request;


import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Rule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос для создания/обновления админа/модератора")
public class AdminManagementRequest {

    @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Невалидная почта. Пример: example@mail.com")
    @NotBlank(message = "еmail не может быть пустым")
    @Schema(description = "Почта", example = "test@mail.ru")
    private String email;

    @NotBlank(message = "role не может быть пустым")
    @Schema(description = "Роль пользователя", example = "ADMIN|MODERATOR")
    private String role;

    @Schema(description = "Права пользователя, указываются для роли MODERATOR")
    private List<String> nameRules;

    @NotNull(message = "access не может быть null")
    @Schema(description = "Право доступа", example = "true")
    private Boolean access;

    @AssertTrue(message = "указана невалидная роль")
    private boolean isRoleValid() {
        for (Admin.ROLE role : Admin.ROLE.values()) {
            if (role.name().equals(this.role)) {
                return true;
            }
        }
        return false;
    }

    @AssertTrue(message = "указано неверное правило")
    private boolean isNameRulesValid() {
        if (nameRules == null) {
            return true;
        }
        for (String rule : nameRules) {
            try {
                Rule.RULE.valueOf(rule);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        return true;
    }
}
