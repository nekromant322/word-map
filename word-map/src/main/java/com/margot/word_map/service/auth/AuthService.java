package com.margot.word_map.service.auth;

import com.margot.word_map.dto.AdminJwtInfo;
import com.margot.word_map.dto.ConfirmCodeDto;
import com.margot.word_map.dto.request.ConfirmRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.RefreshTokenException;
import com.margot.word_map.exception.UserNotAccessException;
import com.margot.word_map.exception.UserNotFoundException;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Confirm;
import com.margot.word_map.model.RefreshToken;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.service.audit.AuditActionType;
import com.margot.word_map.service.audit.AuditService;
import com.margot.word_map.service.email.EmailService;
import com.margot.word_map.service.jwt.JwtService;
import com.margot.word_map.service.refresh_token.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final ConfirmCodeService confirmCodeService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuditService auditService;

    public ConfirmResponse login(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("account not found with email: " + email));

        if (!admin.isAccessGranted()) {
            throw new UserNotAccessException("Аккаунт заблокирован: " + email);
        }

        return generateAndSendConfirm(admin.getId(), email);
    }

    public ConfirmResponse resendConfirm(Long confirmId) {
        Confirm confirm = confirmCodeService.verifyConfirmById(confirmId);

        Admin admin = getActiveAdminById(confirm.getAdminId());
        return generateAndSendConfirm(admin.getId(), admin.getEmail());
    }

    public TokenResponse refreshTokens(String refreshToken, String device) {
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(RefreshTokenException::new);

        if (storedToken.getExpiryAt().isBefore(LocalDateTime.now())) {
            refreshTokenService.deleteRefreshToken(storedToken);
            throw new RefreshTokenException("Срок действия токена обновления истёк");
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

        Admin admin = getActiveAdminById(confirm.getAdminId());
        admin.setDateActive(LocalDateTime.now());

        TokenResponse tokenResponse = generateTokens(admin, userAgent);

        auditService.log(admin.getId(), AuditActionType.ADMIN_LOGGED_IN);

        return tokenResponse;
    }

    @PreAuthorize("hasRole('MODERATOR')")
    @Transactional
    public void logout(String refreshToken) {
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(RefreshTokenException::new);
        refreshTokenService.deleteRefreshToken(storedToken);
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

    private Admin getActiveAdminById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("admin not found with id: " + id));

        if (!admin.isAccessGranted()) {
            throw new UserNotAccessException("Аккаунт заблокирован: " + admin.getEmail());
        }

        return admin;
    }
}

