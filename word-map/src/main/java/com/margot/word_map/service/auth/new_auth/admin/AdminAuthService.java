package com.margot.word_map.service.auth.new_auth.admin;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.ConfirmCodeDto;
import com.margot.word_map.dto.UserDto;
import com.margot.word_map.dto.request.ConfirmEmailRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.InvalidConfirmCodeException;
import com.margot.word_map.exception.InvalidTokenException;
import com.margot.word_map.exception.TokenExpiredException;
import com.margot.word_map.exception.UserNotAccessException;
import com.margot.word_map.model.*;
import com.margot.word_map.service.auth.new_auth.ConfirmCodeService;
import com.margot.word_map.service.email.EmailService;
import com.margot.word_map.service.jwt.JwtService;
import com.margot.word_map.service.refresh_token_service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.cfg.defs.EmailDef;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminService adminService;

    private final ConfirmCodeService confirmCodeService;

    private final EmailService emailService;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    // вход (по почте)
    @Transactional
    public ConfirmResponse loginAdmin(String email) {
        AdminDto adminInfo = adminService.getAdminInfoByEmail(email);

        if (!adminInfo.getAccess()) {
            throw new UserNotAccessException("user has not access");
        }

        ConfirmCodeDto codeDto = confirmCodeService.generateConfirmCode(UserType.ADMIN, adminInfo.getId());
        emailService.sendConfirmEmail(ConfirmEmailRequest.builder()
                .verificationCode(String.valueOf(codeDto.getCode()))
                .email(email)
                .build());

        return new ConfirmResponse(codeDto.getCodeId(), codeDto.getExpirationTime());
    }

    @Transactional
    public TokenResponse verifyConfirmCodeAndGenerateTokens(String email, String codeStr) {
        Integer code = parseCode(codeStr);
        Admin admin = adminService.getAdminByEmail(email);
        confirmCodeService.verifyConfirmCode(code, admin.getId(), UserType.ADMIN);

        String accessToken = jwtService.generateAccessToken(
                email,
                admin.getRole(),
                admin.getRules().stream().map(Rule::getName).toList()
        );
        String refreshToken = refreshTokenService.generateAndSaveRefreshToken(admin.getId(), email);
        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("invalid refresh token"));

        if (storedToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            refreshTokenService.deleteRefreshToken(storedToken);
            throw new TokenExpiredException("refresh token expired");
        }

        Admin admin = adminService.getAdminById(storedToken.getUserId());

        String newAccessToken = jwtService.generateAccessToken(
                admin.getEmail(),
                null,
                null
        );
        return new TokenResponse(newAccessToken, refreshToken);
    }

    private Integer parseCode(String codeStr) {
        try {
            return Integer.parseInt(codeStr);
        } catch (NumberFormatException e) {
            throw new InvalidConfirmCodeException();
        }
    }

    public void logout(Long id) {
        refreshTokenService.deleteRefreshTokenByUserId(id);
    }
}

