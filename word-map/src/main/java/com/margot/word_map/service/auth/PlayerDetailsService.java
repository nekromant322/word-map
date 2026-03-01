package com.margot.word_map.service.auth;

import com.margot.word_map.dto.security.PlayerDetails;
import com.margot.word_map.model.Player;
import com.margot.word_map.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerDetailsService implements UserDetailsService {

    private final PlayerRepository playerRepository;

    @Override
    public PlayerDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return playerRepository.findByEmail(email)
                .map(this::from)
                .orElseThrow(() -> new UsernameNotFoundException("Игрок не найден " + email));
    }

    public PlayerDetails from(Player player) {
        return PlayerDetails.builder()
                .id(player.getId())
                .email(player.getEmail())
                .accessGranted(player.getAccess())
                .authorities(List.of(new SimpleGrantedAuthority("USER")))
                .build();
    }
}
