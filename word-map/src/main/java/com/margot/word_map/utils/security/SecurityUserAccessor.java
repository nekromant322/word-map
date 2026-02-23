package com.margot.word_map.utils.security;

import com.margot.word_map.dto.security.PlayerDetails;
import com.margot.word_map.exception.UserNotFoundException;
import com.margot.word_map.model.User;
import com.margot.word_map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUserAccessor {

    private final UserRepository userRepository;

    public boolean isUser(Authentication authentication) {
        return  authentication != null && authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"));
    }

    public long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof PlayerDetails playerDetails) {
            return playerDetails.getId();
        }
        throw new UserNotFoundException();
    }

    public User getCurrentUser() {
        return userRepository.findById(getCurrentUserId()).orElseThrow(UserNotFoundException::new);
    }
}
