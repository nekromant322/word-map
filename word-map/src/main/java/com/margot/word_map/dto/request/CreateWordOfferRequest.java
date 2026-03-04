package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на создание нового слова в предложке")
public class CreateWordOfferRequest {

    @NotBlank(message = "Слово не может быть пустым")
    @Schema(description = "Слово, добавляемое в словарь", example = "лопата")
    private String word;
}
