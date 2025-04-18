package com.margot.word_map.controller.rest;

import com.margot.word_map.dto.request.ConfirmRequest;
import com.margot.word_map.dto.request.UserSignUpRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.InvalidTokenException;
import com.margot.word_map.model.User;
import com.margot.word_map.service.auth.new_auth.user.UserAuthService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/user")
public class UserAuthController {

    private final UserAuthService userAuthService;

    @PostMapping("/signup")
    public ConfirmResponse signUpUser(@RequestBody @Validated UserSignUpRequest request) {
        return userAuthService.signUpUser(request);
    }

    @GetMapping("/login/{email}")
    public ConfirmResponse sendCode(@PathVariable @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format. Example: example@mail.com")
                                    @Parameter(description = "Почта пользователя", example = "mail123@gmail.com")
                                    @NotBlank String email) {
        return userAuthService.login(email);
    }

    @PostMapping("/confirm")
    public TokenResponse verifyCode(
            @RequestBody @Validated ConfirmRequest confirmRequest
    ) {
        return userAuthService.verifyConfirmCodeAndGenerateTokens(confirmRequest.getEmail(), confirmRequest.getCode());
    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken(
            @Parameter(description = "кука со значением refresh токена")
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            throw new InvalidTokenException("refresh token is missing");
        }

        return userAuthService.refreshAccessToken(refreshToken);
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal UserDetails userDetails) {
        userAuthService.logout(((User) userDetails).getId());
    }
}
