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

    private final AdminService adminService;

    @Autowired
    public AdminAuthService(
            RefreshTokenService refreshTokenService,
            AdminService adminService,
            AdminAuthEntityService adminAuthEntityService,
            ConfirmCodeService confirmCodeService,
            EmailService emailService,
            JwtService jwtService
    ) {
        super(refreshTokenService, adminAuthEntityService, confirmCodeService, emailService, jwtService, UserType.ADMIN);
        this.adminService = adminService;
    }

    @Override
    public TokenResponse verifyConfirmCodeAndGenerateTokens(String email, String codeStr) {
        Integer code = parseCode(codeStr);
        AdminDto adminDto = adminService.getAdminInfoByEmail(email);
        confirmCodeService.verifyConfirmCode(code, adminDto.getId(), userType);

        String accessToken = jwtService.generateAccessToken(adminDto.getEmail(), extractRole(adminDto), extractRules(adminDto));
        String refreshToken = refreshTokenService.generateAndSaveRefreshToken(adminDto.getId(), email, UserType.ADMIN);

        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    protected AdminDto getEntityById(Long id) {
        return adminService.getAdminInfoById(id);
    }

    @Override
    protected RuntimeException createNoAccessException() {
        return new AdminNotAccessException("admin has no access");
    }

    @Override
    protected String extractRole(AdminDto entity) {
        return entity.getRole();
    }

    @Override
    protected List<String> extractRules(AdminDto entity) {
        return entity.getAdminRules().stream()
                .map(RuleDto::getRule)
                .toList();
    }
}
