package com.margot.word_map.service.auth;

import com.margot.word_map.dto.request.ConfirmEmailRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.InvalidTokenException;
import com.margot.word_map.exception.TokenExpiredException;
import com.margot.word_map.model.*;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.repository.ConfirmRepository;
import com.margot.word_map.repository.RefreshTokenRepository;
import com.margot.word_map.service.email.EmailService;
import com.margot.word_map.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class AuthService {

    private final JwtService jwtService;
    private final EmailService emailService;
    private final ConfirmRepository confirmRepository;
    private final AdminRepository adminRepository;
    private final MessageSource messageSource;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${confirm.code-expiration-time}")
    private Integer confirmCodeExpirationTime;

    public ConfirmResponse sendVerificationCode(String email) {
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);

        if (optionalAdmin.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("error.user.not_found"));
        }

        Admin admin = optionalAdmin.get();
        if (!admin.getAccess()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, getMessage("error.user.not_access"));
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

        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("error.user.not_found"));
        }

        Long adminId = admin.get().getId();
        Confirm confirm = confirmRepository.findByUserIdAndCode(adminId, code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        getMessage("error.confirm.not_found")));

        validateConfirmCode(confirm, code);

        confirmRepository.delete(confirm);

        String accessToken = jwtService.generateAccessToken(email, Role.ADMIN.name());
        String refreshToken = generateAndSaveRefreshToken(adminId, email);

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("invalid refresh token"));

        if (storedToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new TokenExpiredException("refresh token expired");
        }

        String email = adminRepository.findById(storedToken.getUserId()).get().getEmail();
        String role = jwtService.extractRole(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(email, role);

        return new TokenResponse(newAccessToken, refreshToken);
    }

    private String generateAndSaveRefreshToken(Long userId, String email) {
        refreshTokenRepository.deleteByUserId(userId);

        String refreshToken = jwtService.generateRefreshToken(email);
        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setToken(refreshToken);
        token.setExpirationTime(LocalDateTime.now().plusDays(14));

        refreshTokenRepository.save(token);
        return refreshToken;
    }

    private Integer parseCode(String codeStr) {
        try {
            return Integer.parseInt(codeStr);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("error.code.invalid"));
        }
    }

    private void validateConfirmCode(Confirm confirm, Integer code) {
        if (!confirm.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("error.code.mismatch"));
        }
        if (confirm.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, getMessage("error.code.expired"));
        }
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, Locale.getDefault());
    }

    private Integer generateRandomCode() {
        return (int) (Math.random() * 900000) + 100000;
    }
}
