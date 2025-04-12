package com.margot.word_map.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.model.Admin;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.service.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final AdminRepository adminRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = (String) Optional.ofNullable(oauthUser.getAttribute("email"))
                .orElse(oauthUser.getAttribute("default_email"));

        Admin admin = adminRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("Администратор с email " + email + " не найден"));

        String accessToken = jwtService.generateAccessToken(email, admin.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(email);

        String jsonResponse = new ObjectMapper().writeValueAsString(new TokenResponse(accessToken, refreshToken));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }
}
