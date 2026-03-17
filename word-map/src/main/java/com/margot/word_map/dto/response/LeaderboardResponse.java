package com.margot.word_map.dto.response;

import com.margot.word_map.dto.LeaderboardRowDto;
import com.margot.word_map.model.enums.Period;
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
@Schema(description = "Ответ с полным списком лидербордов")
public class LeaderboardResponse {

    @Schema(description = "Тип периода таблицы лидеров.")
    private Period period;

    @Schema(description = "Количество минут до окончания периода.")
    private Long end;

    @Schema(description = "Список игроков")
    private List<LeaderboardRowDto> leaderboard;
}

