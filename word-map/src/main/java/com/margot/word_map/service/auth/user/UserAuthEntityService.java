package com.margot.word_map.service.auth.user;

import com.margot.word_map.dto.UserDto;
import com.margot.word_map.service.auth.generic_auth.AuthEntityService;
import org.springframework.stereotype.Service;

@Service
public class UserAuthEntityService implements AuthEntityService<UserDto> {
    private final UserService userService;

    public UserAuthEntityService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDto getByEmail(String email) {
        return userService.getUserInfoByEmail(email);
    }

    @Override
    public Long getId(UserDto user) {
        return user.getId();
    }

    @Override
    public boolean hasAccess(UserDto user) {
        return user.getAccess();
    }

    @Override
    public String getEmail(UserDto user) {
        return user.getEmail();
    }
}

