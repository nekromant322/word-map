package com.margot.word_map.mapper;

import com.margot.word_map.dto.RoleDto;
import com.margot.word_map.model.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    public RoleDto toDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .role(role.getRole().name())
                .level(role.getLevel().name())
                .description(role.getDescription())
                .build();
    }

    public Set<RoleDto> toDto(Set<Role> roles) {
        return roles.stream().map(this::toDto).collect(Collectors.toSet());
    }

    public List<RoleDto> toDto(List<Role> roles) {
        return roles.stream().map(this::toDto).toList();
    }
}
