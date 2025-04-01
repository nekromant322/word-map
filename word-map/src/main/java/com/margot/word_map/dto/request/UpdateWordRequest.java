package com.margot.word_map.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateWordRequest {

    @Min(value = 1, message = "id должен быть положительным")
    private Long id;

    @NotBlank(message = "слово не может быть пустым")
    private String word;

    @NotBlank(message = "описание не может быть пустым")
    private String description;
}
