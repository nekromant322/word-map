package com.margot.word_map.service.player;

import com.margot.word_map.dto.response.PlayerDetailedResponse;
import com.margot.word_map.exception.UserNotFoundException;
import com.margot.word_map.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {
    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private final Long PLAYER_ID =1L;

    @Test
    void testDetailedPlayerInfoReturnsResponseWhenFound(){
        PlayerDetailedResponse response = PlayerDetailedResponse
                .builder()
                .id(PLAYER_ID)
                .name("Test")
                .build();
        when(playerRepository.findDetailedPlayer(PLAYER_ID))
                .thenReturn(Optional.of(response));

        PlayerDetailedResponse result = playerService.getDetailedPlayerInfo(PLAYER_ID);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test");
        verify(playerRepository, times(1)).findDetailedPlayer(PLAYER_ID);
    }

    @Test
    void testDetailedPlayerInfoThrowsExceptionWhenNotFound(){
        when(playerRepository.findDetailedPlayer(PLAYER_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                playerService.getDetailedPlayerInfo(PLAYER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Аккаунт не найден");

        verify(playerRepository).findDetailedPlayer(PLAYER_ID);
    }
}
