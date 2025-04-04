package com.margot.word_map.service;

import com.margot.word_map.dto.RoleDto;
import com.margot.word_map.mapper.RoleMapper;
import com.margot.word_map.model.Role;
import com.margot.word_map.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    public List<RoleDto> getRolesDto() {
        return roleMapper.toDto(roleRepository.findAll());
    }

    public List<Role> getRoles() { return roleRepository.findAll(); }

    public Optional<Role> getRoleByRole(Role.ROLE role) {
        return roleRepository.findRoleByRole(role);
    }
}
