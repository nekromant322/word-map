package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос для подтверждения проверочного кода")
public class ConfirmRequest {

    @Schema(description = "Идентификатор кода подтверждения", example = "12")
    @NotNull(message = "CONFIRM_NOT_FOUND")
    private Long confirmID;

    @Schema(description = "Код проверки, введенный пользователем", example = "894091")
    @NotBlank(message = "CODE_INVALID")
    @Size(min = 6, max = 6, message = "CODE_INVALID")
    private String codeInput;
}