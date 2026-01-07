package com.margot.word_map.service.auth.user;

import com.margot.word_map.dto.ConfirmCodeDto;
import com.margot.word_map.dto.UserDto;
import com.margot.word_map.dto.request.ConfirmRequest;
import com.margot.word_map.dto.request.UserSignUpRequest;
import com.margot.word_map.dto.response.ConfirmResponse;
import com.margot.word_map.exception.UserAlreadyExistsException;
import com.margot.word_map.model.User;
import com.margot.word_map.model.UserType;
import com.margot.word_map.service.auth.ConfirmCodeService;
import com.margot.word_map.service.auth.generic_auth.AbstractAuthService;
import com.margot.word_map.service.email.EmailService;
import com.margot.word_map.service.jwt.JwtService;
import com.margot.word_map.service.refresh_token.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserAuthService extends AbstractAuthService<UserDto> {

    private final UserService userService;

    @Autowired
    public UserAuthService(
            RefreshTokenService refreshTokenService,
            UserService userService,
            UserAuthSubjectServiceImpl userAuthSubjectService,
            ConfirmCodeService confirmCodeService,
            EmailService emailService,
            JwtService jwtService
    ) {
        super(refreshTokenService, userAuthSubjectService, confirmCodeService, emailService, jwtService, UserType.USER);
        this.userService = userService;
    }

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

        ConfirmCodeDto codeDto = confirmCodeService.generateConfirmCode(createdUser.getId());
        emailService.sendConfirmEmail(ConfirmRequest.builder()
                .code(String.valueOf(codeDto.getCode()))
                .email(request.getEmail())
                .build());

        return new ConfirmResponse(codeDto.getCodeId(), codeDto.getExpirationTime());
    }
}

