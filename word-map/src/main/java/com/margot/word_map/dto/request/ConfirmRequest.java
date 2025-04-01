package com.margot.word_map.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConfirmRequest {

    @NotBlank(message = "Invalid. Email can not be blank")
    @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format. Example: example@mail.com")
    private String email;

    @NotBlank(message = "Invalid. Code can not be blank")
    @Size(min = 6, max = 6, message = "Invalid. Code size must be 6")
    private String code;
}