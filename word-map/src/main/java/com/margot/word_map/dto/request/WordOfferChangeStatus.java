package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WordOfferChangeStatus {
    @Schema(description = "Id", example = "1")
    private Long id;
    @Schema(description = "Статус", example = "UNCHECKED")
    private String status;
}
