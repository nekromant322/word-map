package com.margot.word_map.service.auth;

import com.margot.word_map.dto.security.PlayerDetails;
import com.margot.word_map.model.User;
import com.margot.word_map.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public PlayerDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(this::from)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден " + email));
    }

    public PlayerDetails from(User user) {
        return PlayerDetails.builder()
                .id(user.getId())
                .email(user.getEmail())
                .accessGranted(user.getAccess())
                .authorities(List.of(new SimpleGrantedAuthority("USER")))
                .build();
    }
}
