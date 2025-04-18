package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Слово с массивом букв с их координатами")
public class WordAndLettersWithCoordinates {

    @Schema(description = "Слово с массивом букв с их координатами", example = "лопата")
    @NotBlank
    private String word;

    @Schema(description = "Массив букв с их координатами")
    private List<LettersWithCoordinates> lettersWithCoordinates;
}
