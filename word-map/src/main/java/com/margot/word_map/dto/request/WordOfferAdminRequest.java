package com.margot.word_map.dto.request;

import com.margot.word_map.model.WordOfferStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос для получения списка предложенных слов для административной панели.")
public class WordOfferAdminRequest {

    @NotNull
    @Schema(description = "Язык выбранный пользователем в личном кабинете.", example = "1")
    private Long languageId;

    @Schema(description = "Результат ввода в поисковую строку.", example = "абв")
    private String search;

    @Schema(description = "Статус рассмотрения предложенного слова.", example = "approved")
    private WordOfferStatus filterStatus;

    @Schema(description = "Тип сортировки", example = "RATING_LOW")
    private SortingType sortingType;
}
