package com.margot.word_map.service.auth;

import com.margot.word_map.dto.AdminJwtInfo;
import com.margot.word_map.dto.ConfirmCodeDto;
import com.margot.word_map.dto.request.ConfirmRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.InvalidTokenException;
import com.margot.word_map.exception.TokenExpiredException;
import com.margot.word_map.exception.UserNotAccessException;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.RefreshToken;
import com.margot.word_map.model.UserType;
import com.margot.word_map.service.admin.AdminService;
import com.margot.word_map.service.email.EmailService;
import com.margot.word_map.service.jwt.JwtService;
import com.margot.word_map.service.refresh_token.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminService adminService;
    private final ConfirmCodeService confirmCodeService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public ConfirmResponse login(String email) {
        Admin admin = adminService.getAdminByEmail(email);
        if (!admin.isAccessGranted()) {
            throw new UserNotAccessException();
        }

        ConfirmCodeDto codeDto = confirmCodeService.generateConfirmCode(admin.getId());
        emailService.sendConfirmEmail(ConfirmRequest.builder()
                .code(String.valueOf(codeDto.getCode()))
                .email(email)
                .build());

        return new ConfirmResponse(codeDto.getCodeId(), codeDto.getExpirationTime());
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("invalid refresh token"));

        if (storedToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            refreshTokenService.deleteRefreshToken(storedToken);
            throw new TokenExpiredException("refresh token expired");
        }

        Admin admin = adminService.getAdminById(storedToken.getUserId());
        String newAccessToken = jwtService.generateAccessToken(AdminJwtInfo.from(admin));
        return new TokenResponse(newAccessToken, refreshToken);
    }

    public TokenResponse verifyConfirmCodeAndGenerateTokens(String email, String code) {
        Admin admin = adminService.getAdminByEmail(email);
        confirmCodeService.verifyConfirmCode(code, admin.getId());

        String accessToken = jwtService.generateAccessToken(AdminJwtInfo.from(admin));
        String refreshToken = refreshTokenService.generateAndSaveRefreshToken(
                admin.getId(), email, UserType.ADMIN
        );

        return new TokenResponse(accessToken, refreshToken);
    }

    public void logout(Long id) {
        refreshTokenService.deleteRefreshTokenByUserId(id);
    }
}

