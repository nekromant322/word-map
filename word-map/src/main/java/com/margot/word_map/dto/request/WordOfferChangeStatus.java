package com.margot.word_map.dto.request;

import com.margot.word_map.model.WordOfferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WordOfferChangeStatus {
    @Schema(description = "Id", example = "1")
    @NotNull
    private String word;

    @Schema(description = "Статус", example = "check")
    @NotNull
    private WordOfferStatus status;

    @Schema(description = "Id языка", example = "1")
    @NotNull
    private Long LanguageId;
}
