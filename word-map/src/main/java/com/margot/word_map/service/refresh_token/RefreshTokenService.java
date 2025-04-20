package com.margot.word_map.service.refresh_token;

import com.margot.word_map.model.RefreshToken;
import com.margot.word_map.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveRefreshToken(Long userId, String refreshToken) {
        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setToken(refreshToken);
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
