package com.margot.word_map.dto.request;


import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Rule;
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
public class AdminManagementRequest {

    @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Невалидная почта. Пример: example@mail.com")
    @NotBlank(message = "еmail не может быть пустым")
    private String email;

    @NotBlank(message = "role не может быть пустым")
    private String role;

    private List<String> nameRules;

    @NotNull(message = "access не может быть null")
    private Boolean access;

    @AssertTrue(message = "указана невалидная роль")
    public boolean isRoleValid() {
        for (Admin.ROLE role : Admin.ROLE.values()) {
            if (role.name().equals(this.role)) {
                return true;
            }
        }
        return false;
    }

    @AssertTrue(message = "указано неверное правило")
    public boolean isNameRulesValid() {
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
