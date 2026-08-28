package com.margot.word_map.service.leaderboard;

import com.margot.word_map.dto.LeaderboardRowDto;
import com.margot.word_map.dto.request.CreateLeaderboardRequest;
import com.margot.word_map.dto.response.LeaderboardResponse;
import com.margot.word_map.model.enums.Period;
import com.margot.word_map.repository.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LeaderboardService {
    private final LeaderboardRepository leaderboardRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public LeaderboardResponse getLeaderboardList(CreateLeaderboardRequest request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(request.getPeriod(), now);
        LocalDateTime startDate = calculateStartDate(request.getPeriod(), now);
        List<LeaderboardRowDto> playerList
                = leaderboardRepository.findPlayersInLeaderboard(
                request.getPlatformId(),
                request.getLanguageId(),
                startDate,
                getEndOfDay(request.getPeriod(), now));

        for (int i = 0; i < playerList.size(); i++) {
            playerList.get(i).setPosition((long) (i + 1));
        }
        Long minutesRemaining = (endDate == null) ? null : Duration.between(now, endDate).toMinutes();

        return LeaderboardResponse
                .builder()
                .period(request.getPeriod())
                .end(minutesRemaining)
                .leaderboard(playerList)
                .build();
    }

    private LocalDateTime calculateStartDate(Period period, LocalDateTime now) {
        return switch (period) {
            case DAY -> now.with(LocalTime.MIN);
            case WEEK -> now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);
            case MONTH -> now.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
            case ALL -> null;
        };
    }

    private LocalDateTime calculateEndDate(Period period, LocalDateTime now) {
        return switch (period) {
            case DAY -> now.toLocalDate().plusDays(1).atStartOfDay();
            case WEEK -> now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .plusWeeks(1).toLocalDate().atStartOfDay();
            case MONTH -> now.with(TemporalAdjusters.firstDayOfNextMonth())
                    .toLocalDate().atStartOfDay();
            case ALL -> null;
        };
    }

    private LocalDateTime getEndOfDay(Period period, LocalDateTime now) {
        if (period == Period.ALL) {
            return null;
        }
        return now.toLocalDate().plusDays(1).atStartOfDay();
    }
}

