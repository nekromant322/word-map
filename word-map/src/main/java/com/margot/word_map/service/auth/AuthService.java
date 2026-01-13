package com.margot.word_map.service.auth;

import com.margot.word_map.dto.AdminJwtInfo;
import com.margot.word_map.dto.ConfirmCodeDto;
import com.margot.word_map.dto.request.ConfirmRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.RefreshTokenException;
import com.margot.word_map.exception.UserNotAccessException;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Confirm;
import com.margot.word_map.model.RefreshToken;
import com.margot.word_map.service.admin.AdminService;
import com.margot.word_map.service.email.EmailService;
import com.margot.word_map.service.jwt.JwtService;
import com.margot.word_map.service.refresh_token.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new UserNotAccessException("account is blocked: " + email);
        }

        return generateAndSendConfirm(admin.getId(), email);
    }

    public ConfirmResponse resendConfirm(Long confirmId) {
        Confirm confirm = confirmCodeService.verifyConfirmById(confirmId);

        Admin admin = adminService.getActiveAdminById(confirm.getAdminId());
        return generateAndSendConfirm(admin.getId(), admin.getEmail());
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

    @Transactional
    public TokenResponse verifyConfirmCodeAndGenerateTokens(ConfirmRequest request, String userAgent) {
        Confirm confirm = confirmCodeService.verifyConfirmCode(
                request.getConfirmID(),
                request.getCodeInput());

        Admin admin = adminService.getActiveAdminById(confirm.getAdminId());
        admin.setDateActive(LocalDateTime.now());

        return generateTokens(admin, userAgent);
    }

    public void logout(Long id) {
        refreshTokenService.deleteRefreshTokenByAdminId(id);
    }

    private ConfirmResponse generateAndSendConfirm(Long adminId, String email) {
        ConfirmCodeDto codeDto = confirmCodeService.generateConfirmCode(adminId);
        emailService.sendConfirmEmail(codeDto.getCode(), email);

        return new ConfirmResponse(codeDto.getCodeId(), codeDto.getExpirationTime());
    }

    private TokenResponse generateTokens(Admin admin, String userAgent) {
        String accessToken = jwtService.generateAccessToken(AdminJwtInfo.from(admin));
        String refreshToken = refreshTokenService.generateAndSaveRefreshToken(admin, userAgent);

        return new TokenResponse(accessToken, refreshToken);
    }
}

