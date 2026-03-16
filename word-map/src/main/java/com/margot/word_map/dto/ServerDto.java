package com.margot.word_map.dto;

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
@Schema(description = "Сервер")
public class ServerDto {

    @Schema(description = "Уникальный идентификатор записи", example = "23")
    private Long id;

    @Schema(description = "Название платформы сервера", example = "yandex")
    private String platform;

    @Schema(description = "Язык сервера", example = "ru")
    private String language;

    @Schema(description = "Кастомное название сервера", example = "Тест Яндекса")
    private String name;

    @Schema(description = "Счетчик очищений игрового поля", example = "2")
    private Integer wipeCount;

    @Schema(description = "Дата создания сервера в формате DD.MM.YYYY", example = "10.03.2026")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDateTime created;

    @Schema(description = "Дата последней очистки сервера", example = "11.04.2026")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDateTime wiped;

    @Schema(description = "Дата закрытия сервера", example = "24.12.2028")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDateTime closed;

    @Schema(description = "Статус сервера: true - открыт, false - закрыт", example = "false")
    private Boolean open;
}