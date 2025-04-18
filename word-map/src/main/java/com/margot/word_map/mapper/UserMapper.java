package com.margot.word_map.mapper;

import com.margot.word_map.dto.UserDto;
import com.margot.word_map.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .dateCreation(user.getDateCreation())
                .dateActive(user.getDateActive())
                .access(user.getAccess())
                .build();
    }

    public List<UserDto> toDto(List<User> users) {
        return users.stream().map(this::toDto).toList();
    }
}
