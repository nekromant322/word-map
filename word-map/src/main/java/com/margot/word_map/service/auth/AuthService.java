package com.margot.word_map.service.auth;

import com.margot.word_map.dto.request.ConfirmEmailRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.*;
import com.margot.word_map.model.*;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.repository.ConfirmRepository;
import com.margot.word_map.service.email.EmailService;
import com.margot.word_map.service.jwt.JwtService;
import com.margot.word_map.service.refresh_token_service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class AuthService {

    private final JwtService jwtService;
    private final EmailService emailService;
    private final ConfirmRepository confirmRepository;
    private final AdminRepository adminRepository;
    private final RefreshTokenService refreshTokenService;

    @Value("${confirm.code-expiration-time}")
    private Integer confirmCodeExpirationTime;

    public ConfirmResponse sendVerificationCode(String email) {
        Admin admin = adminRepository.findByEmail(email).orElseThrow(
                () -> new AdminNotFoundException("admin with email " + email + "not found"));

        if (!admin.getAccess()) {
            throw new AdminNotAccessException("admin has not access");
        }

        Integer confirmCode = generateRandomCode();
        Confirm confirm = confirmRepository.findByUserId(admin.getId())
                .orElseGet(() -> new Confirm(confirmCode, admin.getId()));
        confirm.setCode(confirmCode);
        confirm.setCreatedAt(LocalDateTime.now());
        confirm.setExpirationTime(LocalDateTime.now().plusSeconds(confirmCodeExpirationTime));
        Confirm savedConfirm = confirmRepository.save(confirm);

        emailService.sendConfirmEmail(new ConfirmEmailRequest(email, confirmCode.toString()));

        return new ConfirmResponse(savedConfirm.getId(), confirmCodeExpirationTime);
    }

    @Transactional
    public TokenResponse verifyCodeAndGenerateToken(String email, String codeStr) {
        Integer code = parseCode(codeStr);

        Admin admin = adminRepository.findByEmail(email).orElseThrow(
                () -> new AdminNotFoundException("admin with email " + email + "not found"));

        Long adminId = admin.getId();
        Confirm confirm = confirmRepository.findByUserIdAndCode(adminId, code)
                .orElseThrow(InvalidConfirmCodeException::new);

        validateConfirmCode(confirm, code);

        confirmRepository.delete(confirm);

        String accessToken = jwtService.generateAccessToken(
                email,
                admin.getRules().stream()
                        .map(Rule::getName)
                        .toList()
        );
        String refreshToken = generateAndSaveRefreshToken(adminId, email);

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse refreshAccessToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("invalid refresh token"));

        if (storedToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            refreshTokenService.deleteRefreshToken(storedToken);
            throw new TokenExpiredException("refresh token expired");
        }

        Admin admin = adminRepository.findById(storedToken.getUserId()).orElseThrow(
                () -> new AdminNotFoundException("admin with id " + storedToken.getUserId() + " not found"));

        String newAccessToken = jwtService.generateAccessToken(
                admin.getEmail(),
                admin.getRules().stream()
                        .map(Rule::getName)
                        .toList()
        );

        return new TokenResponse(newAccessToken, refreshToken);
    }

    private String generateAndSaveRefreshToken(Long userId, String email) {
        refreshTokenService.deleteRefreshTokenByUserId(userId);

        String refreshToken = jwtService.generateRefreshToken(email);
        refreshTokenService.saveRefreshToken(userId, refreshToken);
        return refreshToken;
    }

    private Integer parseCode(String codeStr) {
        try {
            return Integer.parseInt(codeStr);
        } catch (NumberFormatException e) {
            throw new InvalidConfirmCodeException();
        }
    }

    private void validateConfirmCode(Confirm confirm, Integer code) {
        if (!confirm.getCode().equals(code)) {
            throw new InvalidConfirmCodeException();
        }
        if (confirm.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new InvalidConfirmCodeException();
        }
    }

    private Integer generateRandomCode() {
        return (int) (Math.random() * 900000) + 100000;
    }

    public void logout(Long id) {
        refreshTokenService.deleteRefreshTokenByUserId(id);
    }
}
