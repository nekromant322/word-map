package com.margot.word_map.config.authentication;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StrategyManager {
    private final List<AuthenticationStrategy> strategies;

    public void authenticate(HttpServletRequest request, String json) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (AuthenticationStrategy strategy : strategies) {
            if (strategy.supports(request, auth)) {
                strategy.authenticate(request, json);
                break;
            }
        }
    }
}
