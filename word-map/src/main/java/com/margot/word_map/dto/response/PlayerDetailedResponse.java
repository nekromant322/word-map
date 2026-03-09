package com.margot.word_map.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayerDetailedResponse {

    @Schema(description = "Уникальный идентификатор записи таблицы ")
    private Long id;

    @Schema(description = "Уникальный идентификатор игрока на площадке, с которой он пришел.")
    private String uuid;

    @Schema(description = "Никнейм игрока")
    private String name;

    @Schema(description = "Название платформы игрока")
    private String platform;

    @Schema(description = "Название языка игрока.")
    private String language;

    @Schema(description = "Дата и время создания записи в таблице. Формат DD.MM.YYYY 24hh:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdAt;

    @Schema(description = "Дата и время последней активности игрока. Формат DD.MM.YYYY 24hh:mm")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime lastActive;

    @Schema(description = "Счет игрока.")
    private Integer score;

    @Schema(description = "Флаг разрешения доступа. true - доступ разрешен, false - учетная запись заблокирована.")
    private Boolean access;

    @Schema(description = "Количество предложенных слов для словаря игроком.")
    private Long offers;
}
