package com.margot.word_map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Общая структура ошибки, возвращаемая при возникновении исключений")
public class CommonErrorDto {

    @Schema(description = "Код ошибки", example = "404")
    private Integer code;

    @Schema(description = "Сообщение об ошибке", example = "Пользователь не найден")
    private String message;

    @Schema(description = "Дата и время возникновения ошибки", example = "2025-04-12T15:30:00")
    private LocalDateTime date;
}
