package com.margot.word_map.service.auth.new_auth.user;

import com.margot.word_map.dto.ConfirmCodeDto;
import com.margot.word_map.dto.UserDto;
import com.margot.word_map.dto.request.ConfirmEmailRequest;
import com.margot.word_map.dto.request.UserSignUpRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.*;
import com.margot.word_map.model.*;
import com.margot.word_map.service.auth.new_auth.ConfirmCodeService;
import com.margot.word_map.service.email.EmailService;
import com.margot.word_map.service.jwt.JwtService;
import com.margot.word_map.service.refresh_token_service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserService userService;

    private final ConfirmCodeService confirmCodeService;

    private final RefreshTokenService refreshTokenService;

    private final EmailService emailService;

    private final JwtService jwtService;

    // регистрация аккаунта
    @Transactional
    public ConfirmResponse signUpUser(UserSignUpRequest request) {
        if (userService.isUserExistsByEmail(request.getEmail())) {
            log.info("user with email {} already exists", request.getEmail());
            throw new UserAlreadyExistsException("user with email " + request.getEmail() + " already exists");
        }
        if (userService.isUserExistsByUsername(request.getUsername())) {
            log.info("user with username {} already exists", request.getUsername());
            throw new UserAlreadyExistsException("user with username " + request.getUsername() + " already exists");
        }

        User createdUser = userService.createUser(request.getEmail(), request.getUsername());
        log.debug("user with id {} created", createdUser.getId());

        ConfirmCodeDto codeDto = confirmCodeService.generateConfirmCode(UserType.USER, createdUser.getId());
        emailService.sendConfirmEmail(ConfirmEmailRequest.builder()
                .verificationCode(String.valueOf(codeDto.getCode()))
                .email(request.getEmail())
                .build());

        return new ConfirmResponse(codeDto.getCodeId(), codeDto.getExpirationTime());
    }

    // вход (по почте)
    @Transactional
    public ConfirmResponse loginUser(String email) {
        UserDto userInfo = userService.getUserInfoByEmail(email);

        if (!userInfo.getAccess()) {
            throw new UserNotAccessException("user has not access");
        }

        ConfirmCodeDto codeDto = confirmCodeService.generateConfirmCode(UserType.USER, userInfo.getId());
        emailService.sendConfirmEmail(ConfirmEmailRequest.builder()
                .verificationCode(String.valueOf(codeDto.getCode()))
                .email(email)
                .build());

        return new ConfirmResponse(codeDto.getCodeId(), codeDto.getExpirationTime());
    }

    // валидация кода подтвеждения
    @Transactional
    public TokenResponse verifyConfirmCodeAndGenerateTokens(String email, String codeStr) {
        Integer code = parseCode(codeStr);
        UserDto userDto = userService.getUserInfoByEmail(email);
        confirmCodeService.verifyConfirmCode(code, userDto.getId(), UserType.USER);

        String accessToken = jwtService.generateAccessToken(
                email,
                null,
                null
        );
        String refreshToken = refreshTokenService.generateAndSaveRefreshToken(userDto.getId(), email);
        return new TokenResponse(accessToken, refreshToken);
    }

    // обновление refresh токена
    public TokenResponse refreshAccessToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new InvalidTokenException("invalid refresh token"));

        if (storedToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            refreshTokenService.deleteRefreshToken(storedToken);
            throw new TokenExpiredException("refresh token expired");
        }

        User user = userService.getUserById(storedToken.getUserId());

        String newAccessToken = jwtService.generateAccessToken(
                user.getEmail(),
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
