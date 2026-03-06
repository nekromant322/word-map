package com.margot.word_map.dto;

import com.margot.word_map.model.Admin;

import java.util.List;

public record AdminJwtInfo(
        Long id,
        String email,
        String role,
        List<String> rules
) {

    public static AdminJwtInfo from(Admin admin) {
        return new AdminJwtInfo(
                admin.getId(),
                admin.getEmail(),
                admin.getRole().name(),
                admin.getRules().stream()
                        .map(rule -> rule.getName().name())
                        .toList());
    }
}
