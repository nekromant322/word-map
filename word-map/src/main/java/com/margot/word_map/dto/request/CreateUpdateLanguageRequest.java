package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос создания/обновления языка")
public class CreateUpdateLanguageRequest {

    @NotBlank
    @Size(min = 2, max = 2, message = "Префикс языка должен состоять из двух символов")
    @Pattern(regexp = "^[a-zA-Z]+$")
    @Schema(description = "Префикс языка из двух символов", example = "ru")
    private String prefix;

    @Size(max = 50, message = "Название языка должно иметь не более 50 символов")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Название языка должно состоять только из латиницы")
    @Schema(description = "Название языка", example = "russian")
    private String name;
}
