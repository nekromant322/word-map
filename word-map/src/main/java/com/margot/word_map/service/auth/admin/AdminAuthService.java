package com.margot.word_map.service.auth.admin;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.dto.RuleDto;
import com.margot.word_map.dto.response.TokenResponse;
import com.margot.word_map.exception.AdminNotAccessException;
import com.margot.word_map.model.UserType;
import com.margot.word_map.service.auth.ConfirmCodeService;
import com.margot.word_map.service.auth.generic_auth.AbstractAuthService;
import com.margot.word_map.service.email.EmailService;
import com.margot.word_map.service.jwt.JwtService;
import com.margot.word_map.service.refresh_token_service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminAuthService extends AbstractAuthService<AdminDto> {

    @Autowired
    public AdminAuthService(
            RefreshTokenService refreshTokenService,
            AdminAuthEntityService adminAuthEntityService,
            ConfirmCodeService confirmCodeService,
            EmailService emailService,
            JwtService jwtService
    ) {
        super(refreshTokenService, adminAuthEntityService, confirmCodeService, emailService, jwtService, UserType.ADMIN);
    }
}
