package com.margot.word_map.service.refresh_token_service;

import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.RefreshToken;
import com.margot.word_map.model.Role;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.repository.RefreshTokenRepository;
import com.margot.word_map.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final AdminRepository adminRepository;
    private final MessageSource messageSource;

    // Todo Потом добавим и юзеров через UserDetails
    @Transactional
    public TokenResponse generateToken(String email, Admin admin) {

        String accessToken = jwtService.generateAccessToken(email, Role.ADMIN.name());
        String refreshToken = generateAndSaveRefreshToken(admin.getId(), email);

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse refreshAccessToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        getMessage("error.token.invalid")));

        if (storedToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, getMessage("error.token.expired"));
        }

        String email = adminRepository.findById(storedToken.getUserId()).get().getEmail();
        String role = jwtService.extractRole(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(email, role);

        return new TokenResponse(newAccessToken, refreshToken);
    }

    @Transactional
    public void deleteRefreshTokenByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    private String generateAndSaveRefreshToken(Long userId, String email) {
        refreshTokenRepository.deleteByUserId(userId);

        String refreshToken = jwtService.generateRefreshToken(email);
        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setToken(refreshToken);
        token.setExpirationTime(LocalDateTime.now().plusDays(14));

        refreshTokenRepository.save(token);
        return refreshToken;
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }
}
