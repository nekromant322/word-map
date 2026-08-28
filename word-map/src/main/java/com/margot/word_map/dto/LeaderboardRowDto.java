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
public class LeaderboardRowDto {

    @Schema(description = "Позиция игрока в рейтинге.")
    private Long position;

    @Schema(description = "ID игрока в рейтинге.")
    private Long id;

    @Schema(description = "Имя игрока в рейтинге.")
    private String name;

    @Schema(description = "Количество очков игрока за период.")
    private Long score;

    @Schema(description = "Дата и время последнего изменения очков.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime updated;

    @Schema(description = "Статус доступа игрока в игровой мир.")
    private Boolean access;

    public LeaderboardRowDto(Long playerId, String name, Long score, LocalDateTime updated, Boolean access) {
        this.id = playerId;
        this.name = name;
        this.score = score;
        this.updated = updated;
        this.access = access;
    }
}
