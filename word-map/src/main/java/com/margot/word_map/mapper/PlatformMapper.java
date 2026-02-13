package com.margot.word_map.mapper;

import com.margot.word_map.dto.PlatformDto;
import com.margot.word_map.model.Platform;
import org.springframework.stereotype.Component;

@Component
public class PlatformMapper {

    public PlatformDto toDto(Platform platform) {
        return PlatformDto.builder()
                .id(platform.getId())
                .name(platform.getName())
                .build();
    }
}
