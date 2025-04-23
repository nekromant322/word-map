package com.margot.word_map.service.auth.generic_auth;

import com.margot.word_map.dto.ConfirmCodeDto;
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
import com.margot.word_map.service.refresh_token.RefreshTokenService;
import com.margot.word_map.dto.request.ConfirmRequest;

import java.time.LocalDateTime;

public abstract class AbstractAuthService<T> extends AuthService {

    protected final AuthSubjectService<T> authSubjectService;
    protected final ConfirmCodeService confirmCodeService;
    protected final EmailService emailService;
    protected final JwtService jwtService;
    protected final UserType userType;

    protected AbstractAuthService(
            RefreshTokenService refreshTokenService,
            AuthSubjectService<T> authSubjectService,
            ConfirmCodeService confirmCodeService,
            EmailService emailService,
            JwtService jwtService,
            UserType userType
    ) {
        super(refreshTokenService);
        this.authSubjectService = authSubjectService;
        this.confirmCodeService = confirmCodeService;
        this.emailService = emailService;
        this.jwtService = jwtService;
        this.userType = userType;
    }

    @Override
    public ConfirmResponse login(String email) {
        T entity = authSubjectService.getByEmail(email);
        if (!authSubjectService.hasAccess(entity)) {
            throw authSubjectService.createNoAccessException(email);
        }

        ConfirmCodeDto codeDto = confirmCodeService.generateConfirmCode(userType, authSubjectService.getId(entity));
        emailService.sendConfirmEmail(ConfirmRequest.builder()
                .code(String.valueOf(codeDto.getCode()))
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

        T entity = authSubjectService.getEntityById(storedToken.getUserId());
        String newAccessToken = jwtService.generateAccessToken(
                authSubjectService.getEmail(entity),
                authSubjectService.extractRole(entity),
                authSubjectService.extractRules(entity)
        );
        return new TokenResponse(newAccessToken, refreshToken);
    }

    @Override
    public TokenResponse verifyConfirmCodeAndGenerateTokens(String email, String codeStr) {
        Integer code = parseCode(codeStr);
        T entity = authSubjectService.getByEmail(email);
        confirmCodeService.verifyConfirmCode(code, authSubjectService.getId(entity), userType);

        String accessToken = jwtService.generateAccessToken(email, authSubjectService.extractRole(entity), authSubjectService.extractRules(entity));
        String refreshToken = refreshTokenService.generateAndSaveRefreshToken(
                authSubjectService.getId(entity), email, userType
        );

        return new TokenResponse(accessToken, refreshToken);
    }
}

