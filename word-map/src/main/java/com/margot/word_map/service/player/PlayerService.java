package com.margot.word_map.service.player;

import com.margot.word_map.dto.response.PlayerDetailedResponse;
import com.margot.word_map.exception.UserNotFoundException;
import com.margot.word_map.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public PlayerDetailedResponse getDetailedPlayerInfo(Long playerId) {
        return playerRepository.findDetailedPlayer(playerId).orElseThrow(()
                -> new UserNotFoundException("Аккаунт не найден"));
    }
}
