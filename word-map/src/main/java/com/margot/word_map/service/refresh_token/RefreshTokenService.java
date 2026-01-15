package com.margot.word_map.service.refresh_token;

import com.margot.word_map.model.Admin;
import com.margot.word_map.model.RefreshToken;
import com.margot.word_map.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.expiration.refresh-token-expiration}")
    private Duration refreshTokenExpiration;

    @Transactional
    public String generateAndSaveRefreshToken(Admin admin, String device) {
        String rawToken = createRandomToken();
        saveRefreshToken(admin, hash256(rawToken), device);

        return rawToken;
    }

    @Transactional
    protected void saveRefreshToken(Admin admin, String token, String device) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setAdmin(admin);
        refreshToken.setTokenHash(token);
        refreshToken.setDevice(device);
        refreshToken.setExpirationTime(LocalDateTime.now().plus(refreshTokenExpiration));
        refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String rawToken) {
        return refreshTokenRepository.findByTokenHash(hash256(rawToken));
    }

    @Transactional
    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    private String createRandomToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hash256(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
