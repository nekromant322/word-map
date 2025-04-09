package com.margot.word_map.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ответ со списком слов, удовлетворяющих фильтру")
public class DictionaryListResponse {

    @Schema(description = "Список слов", example = "[\"слово1\", \"слово2\"]")
    private List<String> word;
}
