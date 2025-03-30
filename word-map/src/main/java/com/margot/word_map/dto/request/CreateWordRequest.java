package com.margot.word_map.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateWordRequest {

    @NotBlank(message = "слово не может быть пустым")
    private String word;

    @NotBlank(message = "описание слова не может быть пустым")
    private String description;
}
