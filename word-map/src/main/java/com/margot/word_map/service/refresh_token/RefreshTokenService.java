package com.margot.word_map.service.refresh_token;

import com.margot.word_map.model.RefreshToken;
import com.margot.word_map.model.UserType;
import com.margot.word_map.repository.RefreshTokenRepository;
import com.margot.word_map.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtService jwtService;

    @Transactional
    public String generateAndSaveRefreshToken(Long userId, String email, UserType userType) {
        deleteRefreshTokenByUserId(userId);

        String refresh = jwtService.generateRefreshToken(email);
        saveRefreshToken(userId, refresh, userType);

        return refresh;
    }

    @Transactional
    public void saveRefreshToken(Long userId, String refreshToken, UserType userType) {
        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setToken(refreshToken);
        token.setUserType(userType);
        token.setExpirationTime(LocalDateTime.now().plusDays(14));
        refreshTokenRepository.save(token);
    }

    @Transactional
    public void deleteRefreshTokenByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    public Optional<RefreshToken> findByToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }

    @Transactional
    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}
