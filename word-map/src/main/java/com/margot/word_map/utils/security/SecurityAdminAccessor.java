package com.margot.word_map.utils.security;

import com.margot.word_map.dto.security.AdminDetails;
import com.margot.word_map.exception.UserNotFoundException;
import com.margot.word_map.model.Rule;
import com.margot.word_map.model.enums.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SecurityAdminAccessor {

    public Role getRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> Role.valueOf(a.substring(5)))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("role is not set for security user"));
    }

    public Set<Rule.RULE> getRules(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> !a.startsWith("ROLE_"))
                .map(Rule.RULE::valueOf)
                .collect(Collectors.toSet());
    }

    public boolean hasRole(Authentication auth, Role role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role.name()));
    }

    public boolean hasRule(Authentication auth, Rule.RULE rule) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(rule.name()));
    }

    public Long getCurrentAdminId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof AdminDetails adminDetails) {
            return adminDetails.getId();
        }

        throw new UserNotFoundException();
    }
}
