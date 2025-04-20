package com.margot.word_map.service.auth;

import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.InvalidConfirmCodeException;
import com.margot.word_map.service.refresh_token_service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AuthService {

    protected final RefreshTokenService refreshTokenService;

    protected AuthService(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    public abstract ConfirmResponse login(String email);

    public abstract TokenResponse verifyConfirmCodeAndGenerateTokens(String email, String codeStr);

    public abstract TokenResponse refreshAccessToken(String refreshToken);

    public void logout(Long id) {
        refreshTokenService.deleteRefreshTokenByUserId(id);
    }

    protected Integer parseCode(String codeStr) {
        try {
            return Integer.parseInt(codeStr);
        } catch (NumberFormatException e) {
            throw new InvalidConfirmCodeException();
        }
    }
}
