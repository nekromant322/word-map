package com.margot.word_map.dto.request;

import com.margot.word_map.validation.annotation.ConfirmCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Идентификатор кода подтверждения обязателен")
    @Schema(description = "Идентификатор кода подтверждения", example = "12")
    private Long confirmID;

    @ConfirmCode
    @Schema(description = "Код проверки, введенный пользователем", example = "894091")
    private String codeInput;
}