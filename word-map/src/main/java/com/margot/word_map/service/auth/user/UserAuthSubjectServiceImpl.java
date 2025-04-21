package com.margot.word_map.service.auth.user;

import com.margot.word_map.dto.UserDto;
import com.margot.word_map.exception.BaseException;
import com.margot.word_map.exception.UserNotAccessException;
import com.margot.word_map.service.auth.generic_auth.AuthSubjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAuthSubjectServiceImpl implements AuthSubjectService<UserDto> {
    private final UserService userService;

    public UserAuthSubjectServiceImpl(UserService userService) {
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

    @Override
    public UserDto getEntityById(Long id) {
        return userService.getUserInfoById(id);
    }

    @Override
    public BaseException createNoAccessException(String email) {
        return new UserNotAccessException("user " + email + " has no access");
    }

    @Override
    public String extractRole(UserDto entity) {
        return null;
    }

    @Override
    public List<String> extractRules(UserDto entity) {
        return null;
    }
}

