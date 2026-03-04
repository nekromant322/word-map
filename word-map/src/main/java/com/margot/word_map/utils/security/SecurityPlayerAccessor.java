package com.margot.word_map.utils.security;

import com.margot.word_map.dto.security.PlayerDetails;
import com.margot.word_map.exception.UserNotFoundException;
import com.margot.word_map.model.Player;
import com.margot.word_map.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityPlayerAccessor {

    private final PlayerRepository playerRepository;

    public boolean isUser(Authentication authentication) {
        return  authentication != null && authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
    }

    public long getCurrentPlayerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof PlayerDetails playerDetails) {
            return playerDetails.getId();
        }
        throw new UserNotFoundException();
    }

    public Player getCurrentPlayer() {
        return playerRepository.findById(getCurrentPlayerId()).orElseThrow(UserNotFoundException::new);
    }
}
