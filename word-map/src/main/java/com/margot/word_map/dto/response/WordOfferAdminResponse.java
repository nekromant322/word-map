package com.margot.word_map.dto.response;

import com.margot.word_map.dto.PageDto;
import com.margot.word_map.dto.WordOfferPage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Ответ со списком предложенных слов и пагинацией")
public class WordOfferAdminResponse {
    @NotNull
    @Schema(description = "Список всей информации")
    List<WordOfferPage> content;

    @NotNull
    @Schema(description = "Номер страницы и кол-во объектов")
    private PageDto pageable;

    @NotNull
    @Schema(description = "Общее количество страниц.", example = "18")
    private Integer totalPages;

    @NotNull
    @Schema(description = "Общее количество найденных объектов.", example = "55")
    private Integer totalElements;
}
