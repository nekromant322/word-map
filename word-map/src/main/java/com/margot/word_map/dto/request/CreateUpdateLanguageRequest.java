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
    @Size(min = 2, max = 2, message = "FORMAT_ERROR")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "FORMAT_ERROR")
    @Schema(description = "Префикс языка из двух символов", example = "ru")
    private String prefix;

    @Size(max = 50, message = "FORMAT_ERROR")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "FORMAT_ERROR")
    @Schema(description = "Название языка", example = "russian")
    private String name;
}
