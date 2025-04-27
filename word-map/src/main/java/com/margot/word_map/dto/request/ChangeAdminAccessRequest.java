package com.margot.word_map.dto.request;

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
@Schema(description = "Запрос на изменение доступа админа")
public class ChangeAdminAccessRequest {

    @Schema(description = "id админа, доступ которого изменяется", example = "12")
    @NotNull(message = "id не может быть null")
    private Long id;

    @Schema(description = "доступ админа в систему", example = "true")
    @NotNull(message = "access не может быть null")
    private Boolean access;
}
