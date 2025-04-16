package com.margot.word_map.service.auth.new_auth.user;

import com.margot.word_map.dto.UserDto;
import com.margot.word_map.exception.UserNotFoundException;
import com.margot.word_map.mapper.UserMapper;
import com.margot.word_map.model.User;
import com.margot.word_map.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public UserDto getUserInfoById(Long id) {
        return userMapper.toDto(getUserById(id));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.info("user with id {} not found", id);
            return new UserNotFoundException("user with id " + id + " not found");
        });
    }

    public UserDto getUserInfoByEmail(String email) {
        return userMapper.toDto(getUserByEmail(email));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> {
            log.info("user with email {} not found", email);
            return new UserNotFoundException("user with email " + email + " not found");
        });
    }

    public boolean isUserExistsByEmailOrUsername(String email, String username) {
        return userRepository.existsByEmailOrUsername(email, username);
    }

    public boolean isUserExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean isUserExistsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User createUser(String email, String username) {
        User user = User.builder()
                .email(email)
                .username(username)
                .dateCreation(LocalDateTime.now())
                .access(true)
                .build();

        return userRepository.save(user);
    }
}
