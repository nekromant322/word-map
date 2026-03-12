package com.margot.word_map.controller.rest;

import com.margot.word_map.controller.rest.api.LeaderboardApi;
import com.margot.word_map.dto.request.CreateLeaderboardRequest;
import com.margot.word_map.dto.response.LeaderboardResponse;
import com.margot.word_map.service.leaderboard.LeaderboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/leaderboard")
public class LeaderboardController implements LeaderboardApi {
    private final LeaderboardService leaderboardService;

    @PostMapping
    public LeaderboardResponse getLeaderboardList(@RequestBody @Valid CreateLeaderboardRequest request) {
        return leaderboardService.getLeaderboardList(request);
    }
}
