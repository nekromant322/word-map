package com.margot.word_map.service.auth.admin;

import com.margot.word_map.dto.AdminDto;
import com.margot.word_map.model.UserType;
import com.margot.word_map.service.auth.ConfirmCodeService;
import com.margot.word_map.service.auth.generic_auth.AbstractAuthService;
import com.margot.word_map.service.email.EmailService;
import com.margot.word_map.service.jwt.JwtService;
import com.margot.word_map.service.refresh_token.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminAuthService extends AbstractAuthService<AdminDto> {

    @Autowired
    public AdminAuthService(
            RefreshTokenService refreshTokenService,
            AdminAuthSubjectServiceImpl adminAuthSubjectService,
            ConfirmCodeService confirmCodeService,
            EmailService emailService,
            JwtService jwtService
    ) {
        super(
                refreshTokenService,
                adminAuthSubjectService,
                confirmCodeService,
                emailService,
                jwtService,
                UserType.ADMIN
        );
    }
}
