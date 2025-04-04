package com.margot.word_map.repository;

import com.margot.word_map.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findRoleByRole(Role.ROLE role);
}
