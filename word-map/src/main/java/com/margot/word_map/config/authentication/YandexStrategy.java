package com.margot.word_map.config.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.margot.word_map.service.auth.PlayerDetailsService;
import com.margot.word_map.service.signature.PlayerSignatureService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
@RequiredArgsConstructor
public class YandexStrategy implements AuthenticationStrategy {
    private final PlayerSignatureService playerSignatureService;

    private final PlayerDetailsService playerService;

    @Override
    public boolean supports(HttpServletRequest request, Authentication auth) {
        return request.getServletPath().startsWith("/user");
    }

    @Override
    public void authenticate(HttpServletRequest request, String json)
            throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        String username = playerSignatureService.extractEmail(json);
        String signature = request.getHeader("player_signature");
        validatePlayerSignature(signature, json);
        UserDetails details = playerService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authToken
                = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void validatePlayerSignature(String signature, String json)
            throws NoSuchAlgorithmException, InvalidKeyException {
        if (signature == null) {
            throw new BadCredentialsException("Отсутствует подпись или jwt токен");
        }
        if (!playerSignatureService.isValid(signature, json)) {
            throw new BadCredentialsException("Неверная подпись игрока");
        }
    }
}
