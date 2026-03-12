package com.margot.word_map.mapper;

import com.margot.word_map.dto.security.PlayerDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlayerMapper {

    public PlayerDetails buildPlayerDetails(String uuid) {
        return PlayerDetails.builder()
                .uuid(uuid)
                .authorities(List.of(new SimpleGrantedAuthority("USER")))
                .build();
    }
}