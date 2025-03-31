package com.margot.word_map.controller;

import com.margot.word_map.dto.ConfirmRequest;
import com.margot.word_map.dto.ConfirmResponse;
import com.margot.word_map.service.auth_service.AuthService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login/{email}")
    public ResponseEntity<ConfirmResponse> sendCode(@PathVariable @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "Invalid email format. Example: example@mail.com")
                                                    @NotBlank String email) {

        ConfirmResponse response = authService.sendVerificationCode(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> verifyCode(@RequestBody ConfirmRequest confirmRequest) {
        String token = authService.verifyCodeAndGenerateToken(confirmRequest.getEmail(), confirmRequest.getCode());
        return ResponseEntity.ok(token);
    }
}

