package com.margot.word_map.dto.request;

import com.margot.word_map.model.WordOfferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordOffersSortRequest {

    @Schema(description = "Номер страницы", example = "0")
    private int page = 0;
    @Schema(description = "Размер страницы", example = "10")
    private int size = 10;
    @Schema(description = "Сортировка по параметру", example = "createdAt")
    private String sortBy = "createdAt";
    @Schema(description = "Сортировка по возрастанию/убыванию", example = "desc")
    private String sortDir = "desc";
    @Schema(description = "Статус", example = "check")
    private WordOfferStatus status = WordOfferStatus.CHECK;
}
