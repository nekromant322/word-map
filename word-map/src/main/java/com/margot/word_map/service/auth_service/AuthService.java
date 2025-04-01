package com.margot.word_map.service.auth_service;

import com.margot.word_map.dto.request.ConfirmEmailRequest;
import com.margot.word_map.dto.ConfirmResponse;
import com.margot.word_map.dto.TokenResponse;
import com.margot.word_map.model.*;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.repository.ConfirmRepository;
import com.margot.word_map.repository.RefreshTokenRepository;
import com.margot.word_map.repository.UsersRepository;
import com.margot.word_map.service.email_service.EmailService;
import com.margot.word_map.service.jwt_service.JwtService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
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

    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final ConfirmRepository confirmRepository;
    private final AdminRepository adminRepository;
    private final MessageSource messageSource;
    private final RefreshTokenRepository refreshTokenRepository;

    public ConfirmResponse sendVerificationCode(String email) {
        Optional<Admin> optionalAdmin = adminRepository.findByEmail(email);
        Optional<User> optionalUser = usersRepository.findByEmail(email);

        if (optionalAdmin.isEmpty() && optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("error.user.not_found"));
        }

        Admin admin = optionalAdmin.orElse(null);
        User user = optionalUser.orElse(null);

        if ((admin != null && !admin.getAccess()) || (user != null && !user.getAccess())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, getMessage("error.user.not_access"));
        }

        Integer code = generateRandomCode();
        Long userId = admin != null ? admin.getId() : user.getId();

        Confirm confirm = confirmRepository.findByUserId(userId)
                .orElseGet(() -> new Confirm(code, userId));

        confirm.setCode(code);
        confirm.setCreatedAt(LocalDateTime.now());
        confirm.setExpirationTime(LocalDateTime.now().plusSeconds(300));

        Confirm savedConfirm = confirmRepository.save(confirm);

        emailService.sendConfirmEmail(new ConfirmEmailRequest(email, code.toString()));

        return new ConfirmResponse(savedConfirm.getId(), savedConfirm.getExpirationTime());
    }

    @Transactional
    public TokenResponse verifyCodeAndGenerateToken(
            @Email(
                    regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
                    message = "Invalid email format. Example: example@mail.com") @NotBlank String email,
            @NotBlank @Size(min = 6, max = 6) String codeStr) {

        Integer code = parseCode(codeStr);

        Admin admin = adminRepository.findByEmail(email).orElse(null);
        User user = (admin == null) ? usersRepository.findByEmail(email).orElse(null) : null;

        if (admin == null && user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, getMessage("error.user.not_found"));
        }

        Long userId = (admin != null) ? admin.getId() : user.getId();
        Confirm confirm = confirmRepository.findByUserIdAndCode(userId, code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        getMessage("error.confirm.not_found")));

        validateConfirmCode(confirm, code);

        String role = (admin != null) ? Role.ADMIN.name() : Role.USER.name();
        confirmRepository.delete(confirm);

        String accessToken = jwtService.generateAccessToken(email, role);
        String refreshToken = generateAndSaveRefreshToken(userId, email);

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        getMessage("error.token.invalid")));

        if (storedToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, getMessage("error.token.expired"));
        }

        String email = usersRepository.findById(storedToken.getUserId()).get().getEmail();
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
        token.setExpirationTime(LocalDateTime.now().plusDays(30));

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
