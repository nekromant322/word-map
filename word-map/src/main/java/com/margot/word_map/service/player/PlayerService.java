package com.margot.word_map.service.player;

import com.margot.word_map.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public Long countByPlatformId(Long platformId) {
        return playerRepository.countByPlatformId(platformId);
    }
}
