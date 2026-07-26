package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Запрос на получение списка серверов с фильтрацией и сортировкой")
public class ListServerRequest {

    @Schema(description = "Фильтр по статусу сервера.", example = "true")
    private Boolean filterStatus;

    @Schema(description = "Фильтр по платформе сервера", example = "1")
    private Integer filterPlatform;

    @Schema(description = "Фильтр по языку сервера", example = "2")
    private Integer filterLanguage;

    @Schema(description = "Тип сортировки серверов", example = "DATE_LATE")
    private ServerSortingType sortingType;

    @Schema(description = "Произвольный поисковый запрос пользователя", example = "тест")
    private String search;
}