package com.margot.word_map.dto.request;

import com.margot.word_map.model.enums.PatternType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на поиск паттернов с учетом фильтров")
public class PatternSearchRequest {

    @Schema(description = "Тип сортировки", example = "WEIGHT_LOW")
    private PatternSortingType sortingType;

    @Min(1) @Max(999)
    @Schema(description = "Нижняя граница фильтра по весу", example = "1")
    private Short filterWeightMin;

    @Min(1) @Max(999)
    @Schema(description = "Верхняя граница фильтра по весу", example = "999")
    private Short filterWeightMax;

    @Schema(description = "Множественный фильтр по типам паттернов", example = "angular")
    private List<PatternType> filterPatternType;

    @Schema(description = "Признак черновика", example = "true")
    private Boolean filterIsDraft;
}
