package com.margot.word_map.controller;

import com.margot.word_map.dto.ConfirmRequest;
import com.margot.word_map.dto.ConfirmResponse;
import com.margot.word_map.service.auth_service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login/{email}")
    public ResponseEntity<ConfirmResponse> sendCode(@PathVariable String email) {

        if (email == null || email.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Заполните поле");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не соответствует формату");
        }

        ConfirmResponse response = authService.sendVerificationCode(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> verifyCode(@RequestBody ConfirmRequest confirmRequest) {
        String token = authService.verifyCodeAndGenerateToken(confirmRequest.getEmail(), confirmRequest.getCode());
        return ResponseEntity.ok(token);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
}

