package com.margot.word_map.service.auth.generic_auth;

import com.margot.word_map.dto.ConfirmCodeDto;
import com.margot.word_map.dto.UserDto;
import com.margot.word_map.dto.request.ConfirmEmailRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.InvalidTokenException;
import com.margot.word_map.exception.TokenExpiredException;
import com.margot.word_map.model.RefreshToken;
import com.margot.word_map.model.UserType;
import com.margot.word_map.service.auth.AuthService;
import com.margot.word_map.service.auth.ConfirmCodeService;
import com.margot.word_map.service.email.EmailService;
import com.margot.word_map.service.jwt.JwtService;
import com.margot.word_map.service.refresh_token_service.RefreshTokenService;

import java.time.LocalDateTime;
import java.util.List;

public abstract class AbstractAuthService<T> extends AuthService {

    protected final AuthEntityService<T> authEntityService;
    protected final ConfirmCodeService confirmCodeService;
    protected final EmailService emailService;
    protected final JwtService jwtService;
    protected final UserType userType;

    protected AbstractAuthService(
            RefreshTokenService refreshTokenService,
            AuthEntityService<T> authEntityService,
            ConfirmCodeService confirmCodeService,
            EmailService emailService,
            JwtService jwtService,
            UserType userType
    ) {
        super(refreshTokenService);
        this.authEntityService = authEntityService;
        this.confirmCodeService = confirmCodeService;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.userType = userType;
    }

    @Override
    public ConfirmResponse login(String email) {
        T entity = authEntityService.getByEmail(email);
        if (!authEntityService.hasAccess(entity)) {
            throw authEntityService.createNoAccessException(email);
        }

        ConfirmCodeDto codeDto = confirmCodeService.generateConfirmCode(userType, authEntityService.getId(entity));
        emailService.sendConfirmEmail(ConfirmEmailRequest.builder()
                .verificationCode(String.valueOf(codeDto.getCode()))
                .email(email)
                .build());

        return new ConfirmResponse(codeDto.getCodeId(), codeDto.getExpirationTime());
    }

    @Override
    public TokenResponse refreshAccessToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("invalid refresh token"));

        if (storedToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            refreshTokenService.deleteRefreshToken(storedToken);
            throw new TokenExpiredException("refresh token expired");
        }

        T entity = authEntityService.getEntityById(storedToken.getUserId());
        String newAccessToken = jwtService.generateAccessToken(
                authEntityService.getEmail(entity),
                authEntityService.extractRole(entity),
                authEntityService.extractRules(entity)
        );
        return new TokenResponse(newAccessToken, refreshToken);
    }

    @Override
    public TokenResponse verifyConfirmCodeAndGenerateTokens(String email, String codeStr) {
        Integer code = parseCode(codeStr);
        T entity = authEntityService.getByEmail(email);
        confirmCodeService.verifyConfirmCode(code, authEntityService.getId(entity), userType);

        String accessToken = jwtService.generateAccessToken(email, authEntityService.extractRole(entity), null);
        String refreshToken = refreshTokenService.generateAndSaveRefreshToken(authEntityService.getId(entity), email, userType);

        return new TokenResponse(accessToken, refreshToken);
    }
}

