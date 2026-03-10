package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.PlayerApi;
import com.margot.word_map.dto.response.PlayerDetailedResponse;
import com.margot.word_map.service.player.PlayerService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class PlayerController implements PlayerApi {
    private final PlayerService playerService;

    @GetMapping("/{id}")
    public PlayerDetailedResponse getDetailedPlayerInfo(@Parameter(description = "id игрока", example = "10")
                                                            @PathVariable Long id) {
        return playerService.getDetailedPlayerInfo(id);
    }
}
