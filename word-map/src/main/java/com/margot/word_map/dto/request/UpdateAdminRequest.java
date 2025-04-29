package com.margot.word_map.dto.request;

import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Rule;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
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
@Schema(description = "Запрос для обновления админа (роль и права)")
public class UpdateAdminRequest {

    @Schema(description = "id админа", example = "12")
    @NotNull(message = "id не может быть null")
    private Long id;

    @Schema(description = "роль, админ/модератор", example = "ADMIN|MODERATOR")
    @NotBlank(message = "role не может быть пустой")
    private String role;

    @Schema(description = "массив прав, указываются для роли MODERATOR",
            example = "MANAGE_DICTIONARY, " +
                    "WIPE_DICTIONARY, MANAGE_RATING, MANAGE_WORLD," +
                    " MANAGE_ROLE, MANAGE_EVENT, MANAGE_SHOP")
    private List<String> nameRules;

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
