package com.margot.word_map.service.leaderboard;

import com.margot.word_map.dto.LeaderboardRowDto;
import com.margot.word_map.dto.request.CreateLeaderboardRequest;
import com.margot.word_map.dto.response.LeaderboardResponse;
import com.margot.word_map.model.enums.Period;
import com.margot.word_map.repository.LeaderboardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class LeaderboardServiceTest {

    @Mock
    private LeaderboardRepository leaderboardRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    @Test
    void testLeaderboardListAndPositions() {
        CreateLeaderboardRequest request = CreateLeaderboardRequest
                .builder()
                .platformId("test")
                .languageId(1L)
                .period(Period.DAY)
                .build();
        List<LeaderboardRowDto> mockPlayers = List.of(
                new LeaderboardRowDto(1L, "Player 1", 500L, LocalDateTime.now(), true),
                new LeaderboardRowDto(2L, "Player 2", 300L, LocalDateTime.now(), true)
        );

        when(leaderboardRepository.findPlayersInLeaderboard(
                eq("test"), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockPlayers);
        LeaderboardResponse response = leaderboardService.getLeaderboardList(request);

        assertThat(response).isNotNull();
        assertThat(response.getPeriod()).isEqualTo(Period.DAY);
        assertThat(response.getLeaderboard()).hasSize(2);
        verify(leaderboardRepository).findPlayersInLeaderboard(
                eq("test"), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testLeaderboardFindAll() {
        CreateLeaderboardRequest request = CreateLeaderboardRequest
                .builder()
                .platformId("test")
                .languageId(1L)
                .period(Period.ALL)
                .build();

        when(leaderboardRepository.findPlayersInLeaderboard(
                eq("test"), eq(1L), eq(null), eq(null)))
                .thenReturn(Collections.emptyList());

        LeaderboardResponse response = leaderboardService.getLeaderboardList(request);
        assertThat(response.getEnd()).isNull();
        assertThat(response.getLeaderboard()).isEmpty();
    }

    @Test
    void testLeaderboardNextMonth() {
        CreateLeaderboardRequest request = CreateLeaderboardRequest
                .builder()
                .platformId("test")
                .languageId(1L)
                .period(Period.MONTH)
                .build();

        when(leaderboardRepository.findPlayersInLeaderboard(
                eq("test"), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        LeaderboardResponse response = leaderboardService.getLeaderboardList(request);

        ArgumentCaptor<LocalDateTime> startCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(leaderboardRepository).findPlayersInLeaderboard(any(), any(), startCaptor.capture(), any());

        LocalDateTime capturedStart = startCaptor.getValue();

        assertThat(capturedStart.getDayOfMonth()).isEqualTo(1);
        assertThat(capturedStart.toLocalTime()).isEqualTo(LocalTime.MIN);
        assertThat(response.getEnd()).isGreaterThan(0);
    }
}
