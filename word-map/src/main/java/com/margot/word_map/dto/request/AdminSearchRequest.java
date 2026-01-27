package com.margot.word_map.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на поиск пользователей админ панели по фильтрам. Все фильтры опциональны.")
public class AdminSearchRequest {

    @Schema(description = "Данные для поиска по почте без учета регистра", example = "Ivan")
    private String search;

    @Schema(description = "Тип сортировки", example = "ALPHABET", allowableValues = {"ALPHABET", "ROLE", "DATE"})
    private String sortingType;

    @Schema(description = "Фильтр для поиска по роли", example = "admin", allowableValues = {"admin", "moderator"})
    private String filterRole;

    @Schema(description = "Фильтр для поиска по доступному языку", example = "1")
    private Long filterLanguage;

    @Schema(description = "Фильтр по признаку доступа пользователя к административной панели", example = "true")
    private Boolean filterAccess;
}
