package com.margot.word_map.service.auth_service;

import com.margot.word_map.dto.ConfirmEmailRequest;
import com.margot.word_map.dto.ConfirmResponse;
import com.margot.word_map.model.Admin;
import com.margot.word_map.model.Confirm;
import com.margot.word_map.model.Role;
import com.margot.word_map.model.User;
import com.margot.word_map.repository.AdminRepository;
import com.margot.word_map.repository.ConfirmRepository;
import com.margot.word_map.repository.UsersRepository;
import com.margot.word_map.service.email_service.EmailService;
import com.margot.word_map.service.jwt_service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UsersRepository usersRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final ConfirmRepository confirmRepository;
    private final AdminRepository adminRepository;

    public AuthService(UsersRepository usersRepository, JwtService jwtService,
                       EmailService emailService, ConfirmRepository confirmRepository,
                       AdminRepository adminRepository) {
        this.usersRepository = usersRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.confirmRepository = confirmRepository;
        this.adminRepository = adminRepository;
    }

    public ConfirmResponse sendVerificationCode(String email) {
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        User user = admin == null ? usersRepository.findByEmail(email).orElse(null) : null;

        if (admin == null && user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
        }

        if (admin != null && !admin.getAccess()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "USER_NOT_ACCESS");
        } else if (user != null && !user.getAccess()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "USER_NOT_ACCESS");
        }

        Integer code = generateRandomCode();

        Long userId = admin != null ? admin.getId() : user.getId();
        Confirm confirm = confirmRepository.findByUserId(userId)
                .orElse(new Confirm(code, userId));
        confirm.setCode(code);
        confirm.setCreatedAt(LocalDateTime.now());
        confirm.setExpirationTime(LocalDateTime.now().plusSeconds(180));
        Confirm savedConfirm = confirmRepository.save(confirm);

        ConfirmEmailRequest confirmEmailRequest = new ConfirmEmailRequest(email, code.toString());
        emailService.sendConfirmEmail(confirmEmailRequest);

        ConfirmResponse confirmResponse = new ConfirmResponse();
        confirmResponse.setConfirmID(savedConfirm.getId());
        confirmResponse.setLifetime(savedConfirm.getExpirationTime());

        return confirmResponse;
    }

    public String verifyCodeAndGenerateToken(String email, String codeStr) {
        if (codeStr == null || codeStr.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Заполните поле");
        }
        if (codeStr.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Введите весь код");
        }

        Integer code;
        try {
            code = Integer.parseInt(codeStr);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный код");
        }

        Admin admin = adminRepository.findByEmail(email).orElse(null);
        User user = admin == null ? usersRepository.findByEmail(email).orElse(null) : null;

        if (admin == null && user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
        }

        Long userId = admin != null ? admin.getId() : user.getId();
        Confirm confirm = confirmRepository.findByUserIdAndCode(userId, code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CONFIRM_NOT_FOUND"));

        if (!confirm.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CODE_NOT_MATCH");
        }

        if (confirm.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CODE_SPOILED");
        }

        String role = admin != null ? Role.ADMIN.name() : Role.USER.name();
        confirmRepository.delete(confirm);

        return jwtService.generateToken(email, role);
    }

    private Integer generateRandomCode() {
        return (int) (Math.random() * 900000) + 100000;
    }
}
