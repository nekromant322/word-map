package com.margot.word_map.dto.request;

import com.margot.word_map.dto.AdminType;
import com.margot.word_map.model.Role;
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
public class CreateAdminRequest {

    @Email(message = "Неверный формат email")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Некорректный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    @NotNull(message = "Поле access не может быть пустым")
    private Boolean access;

    @NotNull(message = "Поле adminType не может быть пустым")
    private AdminType adminType;

    @NotNull(message = "Роли не могут быть пустыми")
    private List<Role.ROLE> roles;
}