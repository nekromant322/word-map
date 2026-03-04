package com.margot.word_map.config.authentication;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface AuthenticationStrategy {
    boolean supports(HttpServletRequest request, Authentication authentication);

    void authenticate(HttpServletRequest request) throws Exception;
}
