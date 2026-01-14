package com.margot.word_map.service.auth;

import com.margot.word_map.dto.AdminJwtInfo;
import com.margot.word_map.dto.ConfirmCodeDto;
import com.margot.word_map.dto.request.ConfirmRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.RefreshTokenException;
import com.margot.word_map.exception.UserNotAccessException;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.RefreshToken;
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

    public TokenResponse refreshTokens(String refreshToken, String device) {
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(RefreshTokenException::new);

        if (storedToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            refreshTokenService.deleteRefreshToken(storedToken);
            throw new RefreshTokenException("Refresh token expired");
        }

        Admin admin = storedToken.getAdmin();
        refreshTokenService.deleteRefreshToken(storedToken);

        return generateTokens(admin, device);
    }

    public TokenResponse verifyConfirmCodeAndGenerateTokens(String email, String code, String device) {
        Admin admin = adminService.getAdminByEmail(email);
        confirmCodeService.verifyConfirmCode(code, admin.getId());

        return generateTokens(admin, device);
    }

    public void logout(Long id) {
        refreshTokenService.deleteRefreshTokenByAdminId(id);
    }

    private TokenResponse generateTokens(Admin admin, String device) {
        String accessToken = jwtService.generateAccessToken(AdminJwtInfo.from(admin));
        String refreshToken = refreshTokenService.generateAndSaveRefreshToken(admin, device);

        return new TokenResponse(accessToken, refreshToken);
    }
}

