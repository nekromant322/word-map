package com.margot.word_map.service;

import com.margot.word_map.dto.RoleDto;
import com.margot.word_map.mapper.RoleMapper;
import com.margot.word_map.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public List<RoleDto> getRoles() {
        return roleMapper.toDto(roleRepository.findAll());
    }
}
